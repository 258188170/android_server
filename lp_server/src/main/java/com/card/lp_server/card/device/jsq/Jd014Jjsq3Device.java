package com.card.lp_server.card.device.jsq;

import android.util.Log;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.card.lp_server.card.HIDCommunicationUtil;
import com.card.lp_server.card.device.call.VendorDevice;
import com.card.lp_server.card.device.model.Pair;
import com.card.lp_server.card.device.util.ByteUtil;
import com.card.lp_server.card.device.util.HidUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 龙贝设备示例
 *
 * @author SEO-Dev
 */
public class Jd014Jjsq3Device extends VendorDevice {

    private static final String TAG = "Jd014Jjsq3Device";

    /**
     * 获取唯一ID
     */
    @Override
    public String identity() {
        return "014 JSQ2";
    }

    private static boolean isOpen = false;

    @Override
    public Pair<Boolean, String> connect() {
//        Pair<Boolean, String> connect = HidUtils.connect(1155, 22320);
//        isOpen = connect.getFirst();
        return null;
    }

    private static class SingletonHelper {
        private static final Jd014Jjsq3Device INSTANCE = new Jd014Jjsq3Device();
    }

    private boolean checkConnect() {
        return HIDCommunicationUtil.Companion.getInstance().setDevice(1155, 22336).findAndOpenHIDDevice();
    }

    public static Jd014Jjsq3Device getInstance() {
        HIDCommunicationUtil.Companion.getInstance().setDevice(1155, 22320);
        return SingletonHelper.INSTANCE;
    }


    /**
     * 读文件
     *
     * @return 文件数据
     */
//	@DevService("<p>上电获取加电检测模块记录</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
    public String readFile() throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x04;
        req[3] = 0x10;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        Log.d(TAG, "readFile: res1:" + res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "readFile: res2:" + ConvertUtils.bytes2HexString(res));

        String dybh = ConvertUtils.bytes2String(Arrays.copyOfRange(res, 28, 44));
        String dytbh = ConvertUtils.bytes2String(Arrays.copyOfRange(res, 44, 60));
        int jwdztdsj = ByteBuffer.wrap(Arrays.copyOfRange(res, 4, 8)).getInt();
        int jwdbctdsj = ByteBuffer.wrap(Arrays.copyOfRange(res, 10, 12)).getShort();

        int jwdztdcs = ByteBuffer.wrap(Arrays.copyOfRange(res, 14, 16)).getShort();
        int zbdztdsj = ByteBuffer.wrap(Arrays.copyOfRange(res, 16, 20)).getInt();
        int zbdtdsj = ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt();
        int zbdztdcs = ByteBuffer.wrap(Arrays.copyOfRange(res, 26, 28)).getShort();
        Log.d(TAG, "readFile: 导弹编号:" + dybh);
        Log.d(TAG, "readFile: 导引头编号:" + dytbh);
        Log.d(TAG, "readFile: 加温电总通电时间:" + jwdztdsj);
        Log.d(TAG, "readFile: 加温电本次通电时间:" + jwdbctdsj);
        Log.d(TAG, "readFile: 加温电总通电次数:" + jwdztdcs);
        Log.d(TAG, "readFile: 准备电总通电时间:" + zbdztdsj);
        Log.d(TAG, "readFile: 准备电本次通电时间:" + zbdtdsj);
        Log.d(TAG, "readFile: 准备电总通电次数:" + zbdztdcs);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dybh", new String(dybh.getBytes(), StandardCharsets.UTF_8).trim());
        jsonObject.put("dytbh", new String(dytbh.getBytes(), StandardCharsets.UTF_8).trim());
        jsonObject.put("jwdztdsj", jwdztdsj);
        jsonObject.put("jwdbctdsj", jwdbctdsj);
        jsonObject.put("jwdztdcs", jwdztdcs);
        jsonObject.put("zbdztdsj", zbdztdsj);
        jsonObject.put("zbdtdsj", zbdtdsj);
        jsonObject.put("zbdztdcs", zbdztdcs);
        Log.d(TAG, "readFile: " + jsonObject.toString());
        return jsonObject.toString();
    }

    //	@DevService("<p>读取第N次准备电信息</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")

    /**
     * num 第N次加温电内
     * prepare 第N次准备电（每条加温电最多容纳31次准备电，编号1~31）
     */
    public JSONObject readNZBD(int num, int prepare) throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        byte[] req = new byte[64];

        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x08;
        req[3] = 0x10;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
        //第N次加温电内
        byte[] bytes = ByteUtil.intToByteArray(num);
        //第N次准备电（每条加温电最多容纳31次准备电，编号1~31）
        byte[] bytes1 = ByteUtil.intToByteArray(prepare);
        System.arraycopy(bytes, 0, req, 4, bytes.length);
        System.arraycopy(bytes1, 0, req, 8, bytes.length);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        Log.d(TAG, "readNZBD: res1:" + res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "readNZBD: res2:" + res2);
        LogUtils.file("--------读取第N次准备电信息-------");
        LogUtils.file(res);
        String dybh = new String(Arrays.copyOfRange(res, 28, 44), "GBK").trim();
        String dytbh = new String(Arrays.copyOfRange(res, 44, 60), "GBK").trim();
        Log.d(TAG, "readFile: 导弹编号:" + dybh);
        Log.d(TAG, "readFile: 导引头编号:" + dytbh);
        Log.d(TAG, "readFile: 截止到第N次准备电的总通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 16, 20)).getInt());
        Log.d(TAG, "readFile: 第N次准备电通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt());
        Log.d(TAG, "readFile: 截止到第N次的准备电总通电次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 24, 28)).getInt());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dybh", dybh);
        jsonObject.put("dytbh", dytbh);
        jsonObject.put("zbdztdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 16, 20)).getInt());
        jsonObject.put("zbdtdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt());
        jsonObject.put("zbdztdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 24, 28)).getInt());
        return jsonObject;
    }


    //	@DevService("<p>读取第N次加温电信息</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
//    获取第N次加温电加电信息
    public JSONObject readNJWD(int num) throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Thread.sleep(20);
        byte[] req = new byte[64];
        JSONObject jsonObject = new JSONObject();
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x09;
        req[3] = 0x10;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
        //第N次加温电内
        byte[] bytes = ByteUtil.intToByteArray(num);
        System.arraycopy(bytes, 0, req, 4, bytes.length);

        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        Log.d(TAG, "readNJWD: res1:" + res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        String dybh = ConvertUtils.bytes2String(Arrays.copyOfRange(res, 28, 44));
        String dytbh = ConvertUtils.bytes2String(Arrays.copyOfRange(res, 44, 60));
        jsonObject.put("dybh", dybh);
        jsonObject.put("dytbh", dytbh);
        int jwdztdsj = ByteBuffer.wrap(Arrays.copyOfRange(res, 4, 8)).getInt();
        int jwdbctdsj = ByteBuffer.wrap(Arrays.copyOfRange(res, 10, 12)).getShort();
        int jwdztdcs = ByteBuffer.wrap(Arrays.copyOfRange(res, 12, 14)).getShort();
        int gfcs = ByteBuffer.wrap(Arrays.copyOfRange(res, 14, 16)).getShort();
        jsonObject.put("jwdztdsj", jwdztdsj);
        jsonObject.put("jwdbctdsj", jwdbctdsj);
        jsonObject.put("jwdztdcs", jwdztdcs);
        jsonObject.put("gfcs", gfcs);
        Log.d(TAG, "readFile: 导弹编号:" + dybh);
        Log.d(TAG, "readFile: 导引头编号:" + dytbh);
        Log.d(TAG, "readFile: 第 N 次总通电时间:" + jwdztdsj);
        Log.d(TAG, "readFile: 第 N 次本次通电时间:" + jwdbctdsj);
        Log.d(TAG, "readFile: 第 N 次加电次数:" + jwdztdcs);
        Log.d(TAG, "readFile: 挂飞次数:" + gfcs);
        return jsonObject;
    }


    //	@DevService("<p>读取加温电总次数</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
    public JSONObject readJwdzcs() throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Thread.sleep(20);
        byte[] req = new byte[64];

        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = (byte) 0x0b;
        req[3] = 0x10;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        Log.d(TAG, "readJwdzcs: res1:" + res1);
        System.out.println(res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "readJwdzcs: res2:" + res2);
        JSONObject jsonObject = new JSONObject();
        String dybh = new String(Arrays.copyOfRange(res, 12, 28), "GBK").trim();
        String dytbh = new String(Arrays.copyOfRange(res, 28, 44), "GBK").trim();
        jsonObject.put("dybh", dybh);
        jsonObject.put("dytbh", dytbh);
        jsonObject.put("jwdztdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 8, 12)).getInt());
        Log.d(TAG, "readJwdzcs: 加温电总通电次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 8, 12)).getInt());
        return jsonObject;
    }


    /**
     * 写文件
     *
     * @param ddbh 文件数据
     * @return 是否成功
     */
    //设置导弹编号指令
    public boolean writeDdbh(String ddbh) throws InterruptedException, UnsupportedEncodingException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Thread.sleep(20);
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[2 - 1] = 0x55;
        req[3 - 1] = 0x06;
        req[4 - 1] = 0x16;

        req[21 - 1] = 0x55;
        req[22 - 1] = (byte) 0xaa;
        byte[] content = "11223344".getBytes();
        System.arraycopy(content, 0, req, 4, content.length);
        Log.d(TAG, "发送: " + ConvertUtils.bytes2HexString(req));
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        Log.d(TAG, "writeDdbh: res1:" + res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "接收: " + ConvertUtils.bytes2HexString(res));
        return true;
    }


    //	@DevService("<p>写导弹引头号，可传递1个参数</p><ul><li>文件名</li><li>要写入的数据</li></ul><b>返回写入是否成功</b>")
    public boolean writeDyt(String dybh) throws InterruptedException, UnsupportedEncodingException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Thread.sleep(20);
        byte[] req = new byte[64];

        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[2 - 1] = 0x55;
        req[3 - 1] = 0x07;
        req[4 - 1] = 0x16;
        byte[] content = dybh.getBytes();
        System.arraycopy(content, 0, req, 4, content.length);
        req[21 - 1] = 0x55;
        req[22 - 1] = (byte) 0xaa;
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        Log.d(TAG, "writeDyt: res1:" + res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "接收: " + ConvertUtils.bytes2HexString(res));
        return true;
    }


    public boolean writeJD(int jwdztdsj, int jwdbcsj, int jwdztdcs, int zbdztdsj, int zbdbcsj, int zbdztdcs) throws UnsupportedEncodingException, InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        Thread.sleep(20);
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x05;
        req[3] = 0x1e;
        System.arraycopy(ByteUtil.intToByteArray(jwdztdsj), 0, req, 4, 4);
        System.arraycopy(ByteUtil.intToByteArray(jwdbcsj), 0, req, 8, 4);
        System.arraycopy(ByteUtil.intToByteArray(jwdztdcs), 0, req, 12, 4);
        System.arraycopy(ByteUtil.intToByteArray(zbdztdsj), 0, req, 16, 4);
        System.arraycopy(ByteUtil.intToByteArray(zbdbcsj), 0, req, 20, 4);
        System.arraycopy(ByteUtil.intToByteArray(zbdztdcs), 0, req, 24, 4);
        req[28] = 0x55;
        req[29] = (byte) 0xaa;
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        assert res1;
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        Thread.sleep(20);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        String dybh = new String(Arrays.copyOfRange(res, 28, 44), "GBK").trim();
        String dytbh = new String(Arrays.copyOfRange(res, 44, 60), "GBK").trim();
        Log.d(TAG, "readFile: 导弹编号:" + dybh);
        Log.d(TAG, "readFile: 导引头编号:" + dytbh);
        Log.d(TAG, "readFile: 加温电总通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 4, 8)).getInt());
        Log.d(TAG, "readFile: 加温电本次通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 8, 12)).getInt());
        Log.d(TAG, "readFile: 加温电总通电次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 12, 16)).getInt());

        Log.d(TAG, "readFile: 准备电总通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 16, 20)).getInt());
        Log.d(TAG, "readFile: 准备电本次通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt());
        Log.d(TAG, "readFile: 准备电总通电次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 24, 28)).getInt());
        return true;
    }

    public String getJWDList() throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        //获取加温电总次数
        JSONObject jsonObject = readJwdzcs();
        int jwdztdcs = jsonObject.getInt("jwdztdcs");
        assert (jwdztdcs > 0);
        JSONObject jsonObjectResult = new JSONObject();
        jsonObjectResult.put("dybh", jsonObject.getString("dybh"));
        jsonObjectResult.put("dytbh", jsonObject.getString("dytbh"));
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < jwdztdcs; i++) {
            Thread.sleep(10);
            JSONObject jsonObject1 = readNJWD(i);
            jsonArray.put(jsonObject1);
        }
        jsonObjectResult.put("data", jsonArray);
        Log.d(TAG, "getJWDList: " + jsonObjectResult.toString());
        return jsonObjectResult.toString();
    }

    /**
     * 写入履历信息
     * @param inputData
     */
    public static void generateFrames(byte[] inputData) {
        int totalLength = inputData.length;
        int currentIndex = 0;
        int frameNumber = 1;
        int pageNumber = 0;
        int framesInPage = 0;

        while (currentIndex < totalLength) {
            int remainingLength = totalLength - currentIndex;
            int frameDataLength = Math.min(remainingLength, 55);

            byte[] frame = new byte[64];
            // 帧头
            frame[0] = (byte) 0xAA;
            frame[1] = (byte) 0x55;
            // 命令
            frame[2] = (byte) 0x16;
            // 页号
            byte[] bytes = ByteUtil.intToTwoByteArray(pageNumber);
            frame[3] = bytes[0];
            frame[4] = bytes[1];
            // 编号
            frame[5] = (byte) frameNumber;
            // 数据
            System.arraycopy(inputData, currentIndex, frame, 6, frameDataLength);

            currentIndex += frameDataLength;
            frameNumber = (frameNumber % 8) + 1;
            framesInPage++;
            // 在编号的最后一帧上添加帧尾
            frame[61] = (byte) 0x55;
            frame[62] = (byte) 0xAA;
            frame[63] = (byte) 0xBB;

            // 如果是一页的最后一帧，重置编号和帧数
            if (framesInPage == 8 || currentIndex >= totalLength) {
                Log.d(TAG, "generateFrames: read");
                frameNumber = 1;
                framesInPage = 0;
                pageNumber++;
            }

            Log.d("TAG", "generateFrames: " + ConvertUtils.bytes2HexString(frame));
        }
    }

    /**
     * 读取履历信息
     * @param inputData
     * @return
     * @throws InterruptedException
     */
    public boolean processFrames(byte[] inputData) throws InterruptedException {
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

    public byte[] getDataFrames(int pageNumber) throws InterruptedException {
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
    private byte[] concatenateByteArrays(List<byte[]> byteArrayList) {
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
