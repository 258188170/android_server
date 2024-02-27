package com.card.lp_server.card.device.jsq;

import android.util.Log;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.HidUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameProcessor {
    private static final String TAG = "FrameProcessor";

    public static boolean processFrames(byte[] inputData) throws InterruptedException {
        int frameSize = 64;
        int groupSize = 8;
        int totalPages = 16;
        // 计算需要多少组
        int totalGroups = (int) Math.ceil((double) inputData.length / ((frameSize - 9) * groupSize));

        int dataIndex = 0;
        // 外层循环处理每一组
        for (int groupIndex = 0; groupIndex < totalGroups; groupIndex++) {
            // 内层循环处理每一帧
            for (int frameIndex = 0; frameIndex < groupSize; frameIndex++) {
                // 创建一帧的数据
                byte[] frameData = new byte[frameSize];
                Arrays.fill(frameData, (byte) 0);
                // 处理帧头
                frameData[0] = (byte) 0xAA;
                frameData[1] = (byte) 0x55;

                // 处理命令
                frameData[2] = (byte) 0x16;

                // 处理页号
                frameData[3] = (byte) (groupIndex >> 8);
                frameData[4] = (byte) (groupIndex & 0xFF);

                // 处理编号
                frameData[5] = (byte) (frameIndex + 1);

                // 处理数据
                if (dataIndex < inputData.length) {
                    int copyLength = Math.min(frameSize - 6 - 3, inputData.length - dataIndex);
                    Log.d(TAG, "processFrames: copyLength:" + copyLength);
                    System.arraycopy(inputData, dataIndex, frameData, 6, copyLength);
                    dataIndex += copyLength;
                }
                // 处理帧尾
                frameData[61] = (byte) 0x55;
                frameData[62] = (byte) 0xAA;
                frameData[63] = (byte) 0xBB;
                Log.d("TAG", "processFrames:写入: " + ConvertUtils.bytes2HexString(frameData));
                Log.d("TAG", "processFrames:写入: " + ConvertUtils.bytes2String(Arrays.copyOfRange(frameData, 6, 61)));
                Thread.sleep(1000);
                int write = HidUtils.write(frameData);
                assert write > 0;
                if (frameIndex == groupSize - 1) {
                    byte[] res = new byte[frameSize];
                    Arrays.fill(res, (byte) 0);
                    Thread.sleep(2000);
                    int res2 = HidUtils.read(res);
                    assert res2 > 0;
                    Log.d("TAG", "processFrames:结果: " + ConvertUtils.bytes2HexString(res));
                    if (res[4] == (byte) 0xAA && res[5] == (byte) 0xBB) {
                        Log.d("TAG", "processFrames: 发送成功");
                    } else {
                        ToastUtils.showShort("写入失败,请重新上电重试");
                        return false;
                    }
                    // 页号递增
                }
            }
        }
        return true;
    }

    public static byte[] getDataFrames(int pageNumber) throws InterruptedException {
        List<byte[]> totalBytesList = new ArrayList<>();
        for (int i = 0; i < pageNumber; i++) {
            byte[] req = new byte[64];
            Arrays.fill(req, (byte) 0);
            req[0] = (byte) 0xaa;
            req[1] = 0x55;
            req[2] = (byte) 0xA6;
            byte[] bytes = ByteUtil.intToTwoByteArray(i);// 有效范围（0x00 0x00 ~0x0F 0x00）
            req[3] = bytes[0];
            req[4] = bytes[1];
            req[14] = 0x55;
            req[15] = (byte) 0xaa;
            Thread.sleep(200);
            int res1 = HidUtils.write(req);
            assert res1 > 0;
            for (int j = 0; j < 8; j++) {
                Thread.sleep(200);
                byte[] res = new byte[64];
                Arrays.fill(res, (byte) 0);
                int res2 = HidUtils.read(res);
                assert res2 > 0;
                // 将当前的 'res' 数组添加到列表中
                byte[] bytes1 = Arrays.copyOfRange(res, 6, 61);
                Log.d(TAG, "getDataFrames:copyOfRange: " + ConvertUtils.bytes2HexString(bytes1));
                totalBytesList.add(bytes1);
//                Log.d(TAG, "getDataFrames: 结果:" + ConvertUtils.bytes2String(bytes1));
            }

        }
        return concatenateByteArrays(totalBytesList);
    }

    // 将一个字节数组列表连接成一个单一的字节数组
    private static byte[] concatenateByteArrays(List<byte[]> byteArrayList) {
        int totalLength = byteArrayList.stream().mapToInt(arr -> arr.length).sum();
        byte[] result = new byte[totalLength];
        int currentIndex = 0;

        for (byte[] byteArray : byteArrayList) {
            System.arraycopy(byteArray, 0, result, currentIndex, byteArray.length);
            currentIndex += byteArray.length;
        }
        Log.d(TAG, "concatenateByteArrays: " + ConvertUtils.bytes2String(result));
        return result;
    }


    public static boolean clearFlash() throws InterruptedException {
        Log.d(TAG, "clearFlash: ");
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = (byte) 0x43;
        req[3] = 0x46;
        req[4] = 0x53;
        req[5] = 0x63;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
        int res1 = HidUtils.write(req);
        Log.d(TAG, "clearFlash: " + ConvertUtils.bytes2HexString(req));
        assert res1 > 0;
        int index = 0;
        for (int i = 0; i < 10; i++) {
            Thread.sleep(40000);
            byte[] res = new byte[64];
            Arrays.fill(res, (byte) 0);
            int res2 = HidUtils.read(res);
            assert res2 > 0;
            index++;
            Log.d(TAG, "clearFlash: " + ConvertUtils.bytes2HexString(res));
            if (res[0] > 0) {
                return true;
            }
        }
        return false;
    }
}
