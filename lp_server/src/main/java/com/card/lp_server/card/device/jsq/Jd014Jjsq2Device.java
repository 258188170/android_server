package com.card.lp_server.card.device.jsq;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.card.lp_server.card.HIDCommunicationUtil;
import com.card.lp_server.card.device.call.VendorDevice;
import com.card.lp_server.card.device.model.Pair;
import com.card.lp_server.card.device.util.ByteUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 龙贝设备示例
 *
 * @author SEO-Dev
 */
public class Jd014Jjsq2Device extends VendorDevice {

    private static final String TAG = "Jd014Jjsq2Device";

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
//        Pair<Boolean, String> connect = HidUtils.connect(1155, 22336);
//        isOpen = connect.getFirst();
//        isOpen = HidUtils.connect(23112, 22352);
        return null;
    }

    private static class SingletonHelper {
        private static final Jd014Jjsq2Device INSTANCE = new Jd014Jjsq2Device();
    }

    public static Jd014Jjsq2Device getInstance() {
        return SingletonHelper.INSTANCE;
    }
    private boolean checkConnect() {
        return HIDCommunicationUtil.Companion.getInstance().setDevice(1155, 22336).findAndOpenHIDDevice();
    }
    /**
     * 读文件
     *
     * @return 文件数据
     */
//	@DevService("<p>上电获取加电检测模块记录</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
    public String readFile() throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");
        // dev.read(null, 0, 0)
        // dev.write(null, 0, 0)
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
//        int res2 = dev.read(res, 0, res.length);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);

        assert res2;
        LogUtils.file(res);
        Log.d(TAG, "readFile: res2:" + res2);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dybh", dybh);
        jsonObject.put("dytbh", dytbh);
        jsonObject.put("jwdztdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 4, 8)).getInt());
        jsonObject.put("jwdbctdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 8, 12)).getInt());
        jsonObject.put("jwdztdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 12, 16)).getInt());

        jsonObject.put("zbdztdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 16, 20)).getInt());
        jsonObject.put("zbdbctdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt());
        jsonObject.put("zbdztdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 24, 28)).getInt());
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
        ;
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

        System.out.println("加温电N=" + num);
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
        ;
        assert res1;

        Log.d(TAG, "readNJWD: res1:" + res1);
        System.out.println(res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        LogUtils.file("--------读取第N次加温电信息-------");
        LogUtils.file(res);
        String dybh = new String(Arrays.copyOfRange(res, 28, 44), "GBK").trim();
        String dytbh = new String(Arrays.copyOfRange(res, 44, 60), "GBK").trim();
        jsonObject.put("dybh", dybh);
        jsonObject.put("dytbh", dytbh);

        jsonObject.put("jwdztdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 4, 8)).getInt());
        jsonObject.put("jwdbctdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 8, 12)).getInt());
        jsonObject.put("jwdztdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 12, 16)).getInt());

        jsonObject.put("jwdnzbdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 18, 20)).getShort());

        jsonObject.put("zbdztdsj", ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt());
        jsonObject.put("zbdztdcs", ByteBuffer.wrap(Arrays.copyOfRange(res, 24, 28)).getInt());

        Log.d(TAG, "readFile: 导弹编号:" + dybh);
        Log.d(TAG, "readFile: 导引头编号:" + dytbh);
        Log.d(TAG, "readFile: 第 N 次总通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 4, 8)).getShort());
        Log.d(TAG, "readFile: 第 N 次本次通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 8, 12)).getInt());
        Log.d(TAG, "readFile: 第 N 次总通电次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 12, 16)).getInt());
        Log.d(TAG, "readFile: 加温电内准备电总次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 18, 20)).getShort());
        Log.d(TAG, "readFile: 第 N 次准备电的总通电时间:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 20, 24)).getInt());
        Log.d(TAG, "readFile: 第 N 次准备电的总通电次数:" + ByteBuffer.wrap(Arrays.copyOfRange(res, 24, 28)).getInt());
        return jsonObject;
    }


    //	@DevService("<p>读取加温电总次数</p><ul><li>文件名</li></ul><b>返回读取到的数据</b>")
    public JSONObject readJwdzcs() throws UnsupportedEncodingException, InterruptedException, JSONException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        byte[] req = new byte[64];

        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[1] = 0x55;
        req[2] = (byte) 0x0b;
        req[3] = 0x10;
        req[14] = 0x55;
        req[15] = (byte) 0xaa;
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        ;
        assert res1;
        Log.d(TAG, "readJwdzcs: res1:" + res1);
        System.out.println(res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
//        int res2 = dev.read(res, 0, res.length);
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
//	@DevService("<p>写导弹编号，可传递1个参数</p><ul><li>文件名</li><li>要写入的数据</li></ul><b>返回写入是否成功</b>")
    public boolean writeDdbh(String ddbh) throws InterruptedException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        byte[] req = new byte[64];
        Arrays.fill(req, (byte) 0);
        req[0] = (byte) 0xaa;
        req[2 - 1] = 0x55;
        req[3 - 1] = 0x06;
        req[4 - 1] = 0x16;

        byte[] content = ddbh.getBytes();
        System.arraycopy(content, 0, req, 4, content.length);
        req[21 - 1] = 0x55;
        req[22 - 1] = (byte) 0xaa;
//		        int res1 = dev.write(req, 0, req.length);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        ;
        assert res1;
        Log.d(TAG, "writeDdbh: res1:" + res1);
        System.out.println(res1);
        Thread.sleep(20);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "writeDdbh: res2:" + res2);
        return true;
    }


    //	@DevService("<p>写导弹引头号，可传递1个参数</p><ul><li>文件名</li><li>要写入的数据</li></ul><b>返回写入是否成功</b>")
    public boolean writeDyt(String dybh) {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

        // dev.read(null, 0, 0)
        // dev.write(null, 0, 0)
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
//		        int res1 = dev.write(req, 0, req.length);
        boolean res1 = HIDCommunicationUtil.Companion.getInstance().writeToHID(req);
        ;
        assert res1;
        Log.d(TAG, "writeDyt: res1:" + res1);
        System.out.println(res1);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
//		        int res2 = dev.read(res, 0, res.length);
        boolean res2 = HIDCommunicationUtil.Companion.getInstance().readFromHID(res);
        assert res2;
        Log.d(TAG, "writeDyt: res2:" + res2);
        return true;
    }


    public boolean writeJD(int jwdztdsj, int jwdbcsj, int jwdztdcs, int zbdztdsj, int zbdbcsj, int zbdztdcs) throws UnsupportedEncodingException {
        if (!checkConnect()) throw new RuntimeException("设备连接失败,请重试");

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
        ;

        assert res1;
        Log.d(TAG, "writeDyt: res1:" + res1);
        byte[] res = new byte[64];
        Arrays.fill(res, (byte) 0);
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
            JSONArray jsonArray1 = new JSONArray();
            int jwdnzbdcs = jsonObject1.getInt("jwdnzbdcs");
            for (int j = 0; j < jwdnzbdcs; j++) {
                Thread.sleep(10);
                JSONObject jsonObject2 = readNZBD(i, j);
                jsonArray1.put(jsonObject2);
            }
            jsonObject1.put("zbdInfo", jsonArray1);
            jsonArray.put(jsonObject1);
        }
        Log.d(TAG, "getJWDList: " + jsonArray.toString());
        jsonObjectResult.put("data", jsonArray);
        return jsonObjectResult.toString();
    }

}
