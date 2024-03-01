package com.card.lp_server.card.device.jsq;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.card.lp_server.card.HIDCommunicationUtil;
import com.card.lp_server.card.device.call.VendorDevice;
import com.card.lp_server.card.device.kaimai.cama.AESOperation;
import com.card.lp_server.card.device.model.Pair;
import com.card.lp_server.card.device.util.ByteUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 龙贝设备示例
 *
 * @author SEO-Dev
 */
public class Jd014Jjsq1Device extends VendorDevice {

    private static final String TAG = "Jd014Jjsq1Device";

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
//        Pair<Boolean, String> connect = HidUtils.connect(1155, 22352);
//        isOpen = connect.getFirst();
////        isOpen = HidUtils.connect(23112, 22352);
//        return connect;
        return null;
    }

    private static class SingletonHelper {
        private static final Jd014Jjsq1Device INSTANCE = new Jd014Jjsq1Device();
    }

    public static Jd014Jjsq1Device getInstance() {
        return SingletonHelper.INSTANCE;
    }


    /**
     * 读文件
     *
     * @return 文件数据
     */
//	@DevService("<p>上电获取加电检测模块记录</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
    public String readFile() throws UnsupportedEncodingException, InterruptedException {

        // dev.read(null, 0, 0)
        // dev.write(null, 0, 0)
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x10;
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
        JSONObject jsonObject = parseResult(res);
        return jsonObject.toJSONString();
    }

    private static JSONObject parseResult(byte[] res) throws UnsupportedEncodingException {
        byte[] result = new byte[16];
        Arrays.fill(result, (byte) 0);
        assert AESOperation.aes128ofbDecrypt(res, 4, result);
        int bctdsj = ByteBuffer.wrap(Arrays.copyOfRange(result, 1, 5)).getInt();
        int ztdsj = ByteBuffer.wrap(Arrays.copyOfRange(result, 5, 9)).getInt();
        int ztdcs = ByteBuffer.wrap(Arrays.copyOfRange(result, 9, 13)).getInt();
        Arrays.fill(result, (byte) 0);
        assert AESOperation.aes128ofbDecrypt(res, 20, result);
        String mn = new String(result, "GBK").trim();
        String dybh = new String(mn.getBytes(StandardCharsets.UTF_8)).trim();
        Arrays.fill(result, (byte) 0);
        AESOperation.aes128ofbDecrypt(res, 36, result);
        String dyt = new String(result, "GBK").trim();
        String dytbh = new String(dyt.getBytes(StandardCharsets.UTF_8)).trim();
        Log.d(TAG, "parseResult: 导弹编号:" + dybh);
        Log.d(TAG, "parseResult: 导引头编号:" + dytbh);
        Log.d(TAG, "parseResult: 总通电时间:" + ztdsj);
        Log.d(TAG, "parseResult: 本次通电时间:" + bctdsj);
        Log.d(TAG, "parseResult: 总通电次数:" + ztdcs);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dybh", dybh);
        jsonObject.put("dytbh", dytbh);
        jsonObject.put("tdzsj", ztdsj);
        jsonObject.put("bctdsj", bctdsj);
        jsonObject.put("ztdcs", ztdcs);
        Log.d(TAG, "parseResult: " + jsonObject.toJSONString());
        return jsonObject;
    }


    /**
     * 获取第N次上电信息指令
     */
    public JSONObject readNSDXX(int num) throws UnsupportedEncodingException, InterruptedException {
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x15;
        req[3] = 0x10;
        req[4] = (byte) num;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
//        int res1 = HidUtils.write(req);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);

        assert res1;
        Log.d(TAG, "readNZBD: res1:" + res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
//        int res2 = HidUtils.read(res);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);

        assert res2;
        byte[] result = new byte[16];
        Arrays.fill(result, (byte) 0);
        assert AESOperation.aes128ofbDecrypt(res, 4, result);
        int bctdsj = ByteBuffer.wrap(Arrays.copyOfRange(result, 1, 5)).getInt();
        int ztdsj = ByteBuffer.wrap(Arrays.copyOfRange(result, 5, 9)).getInt();
        int ztdcs = ByteBuffer.wrap(Arrays.copyOfRange(result, 9, 13)).getInt();
        Arrays.fill(result, (byte) 0);
//        assert AESOperation.aes128ofbDecrypt(res, 20, result);
//        String mn = new String(result, "GBK").trim();
//        String dybh = new String(mn.getBytes(StandardCharsets.UTF_8)).trim();
//        Arrays.fill(result, (byte) 0);
//        AESOperation.aes128ofbDecrypt(res, 36, result);
//        String dyt = new String(result, "GBK").trim();
//        String dytbh = new String(dyt.getBytes(StandardCharsets.UTF_8)).trim();
//        Log.d(TAG, "parseResult: 导弹编号:" + dybh);
//        Log.d(TAG, "parseResult: 导引头编号:" + dytbh);
        Log.d(TAG, "parseResult: 总通电时间:" + ztdsj);
        Log.d(TAG, "parseResult: 本次通电时间:" + bctdsj);
        Log.d(TAG, "parseResult: 总通电次数:" + ztdcs);
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("dybh", dybh);
//        jsonObject.put("dytbh", dytbh);
        jsonObject.put("tdzsj", ztdsj);
        jsonObject.put("bctdsj", bctdsj);
        jsonObject.put("ztdcs", ztdcs);
        Log.d(TAG, "parseResult: " + jsonObject.toJSONString());
        return jsonObject;
    }

    /**
     * 写文件
     *
     * @param ddbh 文件数据
     * @return 是否成功
     */
//	@DevService("<p>写导弹编号，可传递1个参数</p><ul><li>文件名</li><li>要写入的数据</li></ul><b>返回写入是否成功</b>")
    public boolean writeDdbh(String ddbh) throws InterruptedException, UnsupportedEncodingException {

        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[2 - 1] = 0x55;
        req[3 - 1] = 0x12;
        req[4 - 1] = 0x16;
        byte[] content = ddbh.getBytes();
        assert content.length <= 16;
        System.arraycopy(content, 0, req, 4, content.length);
        req[21 - 1] = 0x55;
        req[22 - 1] = (byte) 0xaa;
//        int res1 = HidUtils.write(req);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);

        assert res1;
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
//        int res2 = HidUtils.read(res);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);

        assert res2;
        Log.d(TAG, "接收: " + ConvertUtils.bytes2HexString(res));
        return true;
    }

    //	@DevService("<p>写导弹引头号，可传递1个参数</p><ul><li>文件名</li><li>要写入的数据</li></ul><b>返回写入是否成功</b>")
    public boolean writeDyt(String dybh) throws UnsupportedEncodingException, InterruptedException {
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[2 - 1] = 0x55;
        req[3 - 1] = 0x13;
        req[4 - 1] = 0x16;
        req[21 - 1] = 0x55;
        req[22 - 1] = (byte) 0xaa;
        byte[] content = dybh.getBytes();
        assert content.length <= 16;
        System.arraycopy(content, 0, req, 4, content.length);
//        int res1 = HidUtils.write(req);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);

        assert res1;
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
//        int res2 = HidUtils.read(res);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);

        assert res2;
        Log.d(TAG, "接收: " + ConvertUtils.bytes2HexString(res));
        return true;
    }


    public boolean writeJDInit(int ztdsj, int bctdsj, int ztdcs) throws UnsupportedEncodingException, InterruptedException {
        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = 0x11;
        req[3] = 0x12;
        req[16] = 0x55;
        req[17] = (byte) 0xaa;
        System.arraycopy(ByteUtil.intToByteArray(ztdsj), 0, req, 4, 4);
        System.arraycopy(ByteUtil.intToByteArray(bctdsj), 0, req, 8, 4);
        System.arraycopy(ByteUtil.intToByteArray(ztdcs), 0, req, 12, 4);
//        int res1 = HidUtils.write(req);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);

        assert res1;
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
//        int res2 = HidUtils.read(res);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);

        assert res2;
        Log.d(TAG, "接收: " + ConvertUtils.bytes2HexString(res));
        return true;
    }

    public String getJWDList() throws UnsupportedEncodingException, InterruptedException {
        String s = readFile();
        JSONObject jsonObject = JSON.parseObject(s);
        int ztdcs = jsonObject.getIntValue("ztdcs");
        JSONObject jsonObjectResult = new JSONObject();
        jsonObjectResult.put("dybh", jsonObject.getString("dybh"));
        jsonObjectResult.put("dytbh", jsonObject.getString("dytbh"));
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ztdcs; i++) {
            JSONObject jsonObject1 = readNSDXX(i);
            jsonObject1.put("dybh", jsonObject.getString("dybh"));
            jsonObject1.put("dytbh", jsonObject.getString("dytbh"));
            jsonArray.add(jsonObject1);
        }
        jsonObjectResult.put("data", jsonArray);
        Log.d(TAG, "getJWDList: " + jsonArray.toJSONString());
        LogUtils.file(jsonObjectResult);
        return jsonObjectResult.toJSONString();
    }

}
