package com.card.lp_server.card.device.kaimai;




import static com.card.lp_server.card.device.util.HidUtils.ACTION_USB_PERMISSION;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.card.lp_server.card.device.kaimai.cama.AESOperation;
import com.card.lp_server.card.device.kaimai.cama.Constants;
import com.card.lp_server.card.device.kaimai.cama.DetectResultStore;
import com.card.lp_server.card.device.kaimai.cama.ResultTable;
import com.card.lp_server.card.device.model.Pair;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

public class UsbUtils {
    private static final String TAG = "UsbUtils";

    /**
     * 抓取凯迈 的数据
     *
     * @param mUsbManager
     * @return
     */
    public static Pair<Boolean, String> getkmResponse(UsbManager mUsbManager) {
        UsbDevice mUsbDevice = null;
        UsbInterface mUsbInterface = null;
        UsbDeviceConnection mDeviceConnection = null;
        StringBuilder msg = new StringBuilder();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                String idetified = device.getProductId() + "-" + device.getVendorId();
                String rightDevice = Constants.hidMap.get(Constants.SBCS_KAIMAI);
                //匹配了
                if (idetified.equals(rightDevice)) {
                    mUsbDevice = device;
                    mUsbInterface = mUsbDevice.getInterface(0);
                    // 判断是否有权限
                    if (mUsbManager.hasPermission(mUsbDevice)) {
                        // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                        mDeviceConnection = mUsbManager.openDevice(mUsbDevice);

                        if (mDeviceConnection == null) {
                            Log.d(TAG, "connect fail");
                            break;
                        }
                        if (mDeviceConnection.claimInterface(mUsbInterface, true)) {
                            DetectResultStore dev = new DetectResultStore(mDeviceConnection, mUsbInterface);
                            try {
                                JSONObject josonObject = new JSONObject();
                                JSONArray jSONArray = new JSONArray();
                                ResultTable[] results = dev.readAll(msg);
                                Log.d(TAG, "共读取到" + results.length + "型弹药检测数据");
                                for (ResultTable result : results) {
                                    msg.append(result.toString());
                                    jSONArray.add(result);
                                }
                                josonObject.put("datas", results);
                                Log.d(TAG, "读取完毕: \n " + josonObject.toJSONString());
                                mDeviceConnection.close();
                                return new Pair<>(true, josonObject.toJSONString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return new Pair<>(false, "Data parsing failed");
                            }
                        } else {
                            mDeviceConnection.close();
                        }
                    } else {
                        Log.d(TAG, "'no Permission'");
                        PendingIntent pi = PendingIntent.getBroadcast(ActivityUtils.getTopActivity(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                        mUsbManager.requestPermission(device, pi);
                        return new Pair<>(false, "no Permission");
                    }
                    break;
                } else {
                    Log.d(TAG, "VID,PID 不匹配 ");
                    return new Pair<>(false, "VID,PID 不匹配 ");
                }
            }
        } else {
            Log.d(TAG, "'no device'");
            return new Pair<>(false, "no device");
        }
        Log.d(TAG, "读取失败! ");
        return new Pair<>(false, "no data");

    }


    public static Pair<Boolean, String> get014Response(UsbManager mUsbManager) {
        UsbDevice mUsbDevice = null;
        UsbInterface mUsbInterface = null;
        UsbDeviceConnection mDeviceConnection = null;
        StringBuilder msg = new StringBuilder();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        boolean isSuccess = false;
        String message = "连接成功！";
        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                msg.append(device.getProductId() + "-" + device.getVendorId());
                Log.d(TAG, device.getProductId() + "-" + device.getVendorId());
                String idetified = device.getProductId() + "-" + device.getVendorId();
                String rightDevice = Constants.hidMap.get(Constants.SBCS_014);
//                msg.append("rightDevice:" + rightDevice);
                Log.d(TAG, "rightDevice:" + rightDevice);
                //匹配了
                if (idetified.equals(rightDevice)) {
                    msg.append("mached:");
                    mUsbDevice = device;
                    mUsbInterface = mUsbDevice.getInterface(0);
                    // 判断是否有权限
                    if (mUsbManager.hasPermission(mUsbDevice)) {
                        // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                        UsbEndpoint inEndpoint = mUsbInterface.getEndpoint(0);  //读数据节点

                        msg.append("max:" + inEndpoint.getMaxPacketSize() + "$$");
                        UsbEndpoint outEndpoint = mUsbInterface.getEndpoint(1); //写数据节点
                        mDeviceConnection = mUsbManager.openDevice(mUsbDevice);

                        if (mDeviceConnection == null) {
                            msg.append("connect fail");
                        }
                        message = "connect success";
                        Log.d(TAG, "get014Response: connect success");
                        if (mDeviceConnection.claimInterface(mUsbInterface, true)) {
                            message = "connect success2";
                            Log.d(TAG, "get014Response: connect success2");
                            isSuccess = true;
                            //进行逻辑读取数据
                            byte[] req = new byte[64];
                            byte[] dst = new byte[16];
                            Arrays.fill(req, (byte) 0);
                            req[1 - 1] = (byte) 0xaa;
                            req[2 - 1] = 0x55;
                            req[3 - 1] = 0x10;
                            req[4 - 1] = 0x10;
                            req[15 - 1] = 0x55;
                            req[16 - 1] = (byte) 0xaa;

                            int ret = -1;
                            int maxTimes = 3;
                            int retime = 0;
                            while ((ret = mDeviceConnection.bulkTransfer(outEndpoint, req, req.length, 100)) < 0) {
                                retime++;
                                if (retime > maxTimes) {
                                    break;
                                }
                            }
                            msg.append("sen data" + ret);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ret = -1;
                            retime = 0;
                            byte[] res = new byte[64];
                            Arrays.fill(res, (byte) 0);

                            ret = mDeviceConnection.bulkTransfer(inEndpoint, res, inEndpoint.getMaxPacketSize(), 30);

                            msg.append("recv:" + ret + "(" + retime + "**");
                            msg.append(Constants.print(res, 64)).append("@@");

                            if (!AESOperation.aes128ofbDecrypt(res, 4, dst)) {
                                msg.append("解密失败");
                                Log.d(TAG, "get014Response: 解密失败");
                            }

                            try {
                                msg.append(Constants.print(dst, 16)).append("###");

                                // 首字节固定为0x11
                                // 获取本次通电时间、总通电时间和通电次数
                                int[] nums = new int[]{
                                        ((((((dst[1] & 0xff) << 8) | (dst[2] & 0xff)) << 8) | (dst[3] & 0xff)) << 8) | (dst[4] & 0xff),
                                        ((((((dst[5] & 0xff) << 8) | (dst[6] & 0xff)) << 8) | (dst[7] & 0xff)) << 8) | (dst[8] & 0xff),
                                        ((((((dst[9] & 0xff) << 8) | (dst[10] & 0xff)) << 8) | (dst[11] & 0xff)) << 8)
                                                | (dst[12] & 0xff)};
//                                msg.append("本次通电22: %d秒" + ((dst[1] & 0xff) | (dst[2] & 0xff) | (dst[3] & 0xff) | (dst[4] & 0xff)) + "\n");
                                Log.d(TAG, "get014Response: " + "本次通电22: %d秒" + ((dst[1] & 0xff) | (dst[2] & 0xff) | (dst[3] & 0xff) | (dst[4] & 0xff)) + "\n");
                                // 获取弹号和导引头号
                                AESOperation.aes128ofbDecrypt(res, 20, dst);
                                msg.append(Constants.print(dst, 16)).append("###");
                                String mn = new String(dst, "GBK");
                                mn = new String(mn.getBytes(StandardCharsets.UTF_8));
                                AESOperation.aes128ofbDecrypt(res, 36, dst);
                                msg.append(Constants.print(dst, 16)).append("###");
                                String tn = new String(dst, "GBK").trim();
                                tn = new String(tn.getBytes(StandardCharsets.UTF_8));
                                DecimalFormat nf = new DecimalFormat("0000.000");
                                Log.d(TAG, "get014Response: \n" + "弹    号: " + mn + "\n" + "弹    号: "
                                        + mn + "\n" + "弹    号: " + mn + "\n" + "总 通 电: " + nums[1] / 3600 + ","
                                        + (nums[1] % 3600) / 60 + "," + nums[1] % 60 + "通电次数:" + nums[2]);
                                msg.append("弹    号: " + mn + "\n");
                                msg.append("弹    号: " + mn + "\n");
                                msg.append("弹    号: " + mn + "\n");
                                msg.append("总 通 电: " + nums[1] / 3600 + "," + (nums[1] % 3600) / 60 + "," + nums[1] % 60);
                                msg.append("通电次数:" + nums[2]);
                                JSONObject jo = new JSONObject();
                                jo.put("dybh", mn);
                                jo.put("dyt", tn);
                                jo.put("bctdsj", nums[0]);
                                jo.put("ztdsjs", nums[1] / 3600);
                                jo.put("ztdsjf", (nums[1] % 3600) / 60);
                                jo.put("ztdsjm", nums[1] % 60);
                                jo.put("tdcs", nums[2]);

                                msg.setLength(0);
                                msg.append(jo.toJSONString());
                                Constants.addDebugMsg(jo.toJSONString());
                                mDeviceConnection.close();
                                return new Pair<>(true, jo.toJSONString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "get014Response: " + e.getMessage());
                                return new Pair<>(false, "Data parsing failed");
                            }
                        } else {
                            mDeviceConnection.close();
                        }
                    } else {
                        Log.d(TAG, "get014Response: no permission");
                        PendingIntent pi = PendingIntent.getBroadcast(ActivityUtils.getTopActivity(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                        mUsbManager.requestPermission(device, pi);
                        return new Pair<>(false, "no permission");
                    }
                    break;
                }
            }
        } else {
            Log.d(TAG, "get014Response: no device");
            return new Pair<>(false, "no device");
        }
        return new Pair<>(false, "no data");

    }

    public static Pair<Boolean, String> get014JsQ2Response(UsbManager mUsbManager) {
        UsbDevice mUsbDevice = null;
        UsbInterface mUsbInterface = null;
        UsbDeviceConnection mDeviceConnection = null;
        StringBuilder msg = new StringBuilder();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        boolean isSuccess = false;
        String message = "连接成功！";
        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                msg.append(device.getProductId() + "-" + device.getVendorId());
                Log.d(TAG, "get014JsQ2Response: " + device.getProductId() + "-" + device.getVendorId());
                String idetified = device.getProductId() + "-" + device.getVendorId();
                String rightDevice = Constants.hidMap.get(Constants.SBCS_014_JSQ2);
                msg.append("rightDevice:" + rightDevice);
                Log.d(TAG, "get014JsQ2Response: " + "rightDevice:" + rightDevice);
                //匹配了
                if (idetified.equals(rightDevice)) {
                    msg.append("mached:");
                    mUsbDevice = device;
                    mUsbInterface = mUsbDevice.getInterface(0);
                    // 判断是否有权限
                    if (mUsbManager.hasPermission(mUsbDevice)) {
                        // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                        UsbEndpoint inEndpoint = mUsbInterface.getEndpoint(0);  //读数据节点
                        msg.append("max:" + inEndpoint.getMaxPacketSize() + "$$");
                        UsbEndpoint outEndpoint = mUsbInterface.getEndpoint(1); //写数据节点
                        mDeviceConnection = mUsbManager.openDevice(mUsbDevice);

                        if (mDeviceConnection == null) {
                            msg.append("connect fail");
                        }
                        message = "connect success";
                        if (mDeviceConnection.claimInterface(mUsbInterface, true)) {
                            message = "connect success2";
                            isSuccess = true;
                            //进行逻辑读取数据
                            byte[] req = new byte[64];
                            byte[] dst = new byte[16];
                            byte[] dst2 = new byte[12];
                            Arrays.fill(req, (byte) 0);
                            req[1 - 1] = (byte) 0xaa;
                            req[2 - 1] = 0x55;
                            req[3 - 1] = 0x04;
                            req[4 - 1] = 0x10;
                            req[15 - 1] = 0x55;
                            req[16 - 1] = (byte) 0xaa;

                            int ret = -1;
                            int maxTimes = 3;
                            int retime = 0;
                            while ((ret = mDeviceConnection.bulkTransfer(outEndpoint, req, req.length, 100)) < 0) {
                                retime++;
                                if (retime > maxTimes) {
                                    break;
                                }
                            }
                            msg.append("sen data" + ret);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ret = -1;
                            retime = 0;
                            byte[] res = new byte[64];
                            Arrays.fill(res, (byte) 0);

                            ret = mDeviceConnection.bulkTransfer(inEndpoint, res, inEndpoint.getMaxPacketSize(), 30);

                            msg.append("recv:" + ret + "(" + retime + "**");
                            msg.append(Constants.print(res, 64)).append("@@");

                           /*if (!AESOperation.aes128ofbDecrypt(res, 4, dst)) {
                                msg.append("解密失败");
                            }*/
                            System.arraycopy(res, 4, dst2, 0, 12);

                            try {
                                msg.append(Constants.print(dst2, 12)).append("###");
                                // 首字节固定为0x11
                                // 获取本次通电时间、总通电时间和通电次数
                                int[] nums = new int[]{
                                        (((dst2[0] & 0xff) << 24) | ((dst2[1] & 0xff) << 16) | ((dst2[2] & 0xff) << 8) | (dst2[3] & 0xff)) * 5,
                                        (((dst2[4] & 0xff) << 24) | ((dst2[5] & 0xff) << 16) | ((dst2[6] & 0xff) << 8) | (dst2[7] & 0xff)) * 5,
                                        ((dst2[8] & 0xff) << 24) | ((dst2[9] & 0xff) << 16) | ((dst2[10] & 0xff) << 8) | (dst2[11] & 0xff)};
                                msg.append("加温电：\n");
                                msg.append("总通电：" + nums[0] + "\n");
                                msg.append("本次通电：" + nums[1] + "\n");
                                msg.append("总通电次数：" + nums[2] + "\n");
                                //取准备电信息
                                // 获取弹号和导引头号
                                System.arraycopy(res, 16, dst2, 0, 12);
                                int[] nums2 = new int[]{
                                        (((dst2[0] & 0xff) << 24) | ((dst2[1] & 0xff) << 16) | ((dst2[2] & 0xff) << 8) | (dst2[3] & 0xff)) * 5,
                                        (((dst2[4] & 0xff) << 24) | ((dst2[5] & 0xff) << 16) | ((dst2[6] & 0xff) << 8) | (dst2[7] & 0xff)) * 5,
                                        ((dst2[8] & 0xff) << 24) | ((dst2[9] & 0xff) << 16) | ((dst2[10] & 0xff) << 8) | (dst2[11] & 0xff)};
                                msg.append("加电时间：\n");
                                msg.append("总通电：" + nums2[0] + "\n");
                                msg.append("本次通电：" + nums2[1] + "\n");
                                msg.append("总通电次数：" + nums2[2] + "\n");
                                //AESOperation.aes128ofbDecrypt(res, 28, dst);
                                System.arraycopy(res, 28, dst, 0, 16);
                                msg.append(Constants.print(dst, 16)).append("###");
                                String mn = new String(dst, "GBK");
                                mn = new String(mn.getBytes(StandardCharsets.UTF_8)).trim();
                                ;
                                //AESOperation.aes128ofbDecrypt(res, 44, dst);
                                System.arraycopy(res, 44, dst, 0, 16);
                                msg.append(Constants.print(dst, 16)).append("###");
                                String tn = new String(dst, "GBK").trim();
                                tn = new String(tn.getBytes(StandardCharsets.UTF_8));
                                DecimalFormat nf = new DecimalFormat("0000.000");
                                msg.append("弹    号: " + mn + "\n");
                                msg.append("导 引 头: " + tn + "\n");
                                msg.append("本次通电: %d秒" + nums[0] + "\n");
                                msg.append("总 通 电: " + nums[1] / 3600 + "," + (nums[1] % 3600) / 60 + "," + nums[1] % 60);
                                msg.append("通电次数:" + nums[2]);
                                JSONObject jo = new JSONObject();
//                                    jo.put("dybh", Constants.GLOBAL_DYBH);
                                jo.put("dybh", mn);
                                jo.put("dyt", tn);
                                jo.put("bctdsj", nums[1]);
                                jo.put("ztdsjs", nums[0] / 3600);
                                jo.put("ztdsjf", (nums[0] % 3600) / 60);
                                jo.put("ztdsjm", nums[0] % 60);
                                jo.put("tdcs", nums[2]);

                                jo.put("bctdsjs", nums[1] / 3600);
                                jo.put("bctdsjf", (nums[1] % 3600) / 60);
                                jo.put("bctdsjm", nums[1] % 60);

                                jo.put("zjdtdsj", nums2[0]);
                                jo.put("zjdtdsjs", nums2[0] / 3600);
                                jo.put("zjdtdsjf", (nums2[0] % 3600) / 60);
                                jo.put("zjdtdsjm", nums2[0] % 60);

                                jo.put("bcjdtdsj", nums[1]);
                                jo.put("bcjdtdsjs", nums2[1] / 3600);
                                jo.put("bcjdtdsjf", (nums[1] % 3600) / 60);
                                jo.put("bcjdtdsjm", nums2[1] % 60);

                                jo.put("zjdtdcs", nums2[2]);
                                Constants.addDebugMsg("msg:" + msg.toString());
                                msg.setLength(0);
                                msg.append(jo.toJSONString());
                                Log.d(TAG, "get014JsQ2Response: \n" + jo.toJSONString());
                                Constants.addDebugMsg(jo.toJSONString());
                                mDeviceConnection.close();
                                return new Pair<>(true, jo.toJSONString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.append("jiex fail " + e.getMessage());
                                Log.e(TAG, "get014JsQ2Response: fail :" + e.getMessage());
                                return new Pair<>(false, "Data parsing failed");
                            }
                        } else {
                            mDeviceConnection.close();
                        }
                    } else {
                        msg.append("no permission");
                        Log.d(TAG, "get014JsQ2Response: no permission");
                        PendingIntent pi = PendingIntent.getBroadcast(ActivityUtils.getTopActivity(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                        mUsbManager.requestPermission(device, pi);
                        return new Pair<>(false, "no permission");
                    }
                    break;
                }
            }
        } else {
            msg.append("no devide ");
            return new Pair<>(false, "no device");
        }
        return new Pair<>(false, "no data");
    }

    public static Pair<Boolean, String> get014JsQ3Response(UsbManager mUsbManager) {
        UsbDevice mUsbDevice = null;
        UsbInterface mUsbInterface = null;
        UsbDeviceConnection mDeviceConnection = null;
        StringBuilder msg = new StringBuilder();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        boolean isSuccess = false;
        String message = "连接成功！";

        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                msg.append(device.getProductId() + "-" + device.getVendorId());
                Log.d(TAG, "get014JsQ3Response: " + device.getProductId() + "-" + device.getVendorId());
                String idetified = device.getProductId() + "-" + device.getVendorId();
                String rightDevice = Constants.hidMap.get(Constants.SBCS_014_JSQ3);
                msg.append("rightDevice:" + rightDevice);
                Log.d(TAG, "get014JsQ3Response: " + "rightDevice:" + rightDevice);
                //匹配了
                if (idetified.equals(rightDevice)) {
                    msg.append("mached:");
                    mUsbDevice = device;
                    mUsbInterface = mUsbDevice.getInterface(0);
                    // 判断是否有权限
                    if (mUsbManager.hasPermission(mUsbDevice)) {
                        // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                        UsbEndpoint inEndpoint = mUsbInterface.getEndpoint(0);  //读数据节点

                        msg.append("max:" + inEndpoint.getMaxPacketSize() + "$$");
                        UsbEndpoint outEndpoint = mUsbInterface.getEndpoint(1); //写数据节点
                        mDeviceConnection = mUsbManager.openDevice(mUsbDevice);

                        if (mDeviceConnection == null) {
                            msg.append("connect fail");
                        }
                        message = "connect success";
                        if (mDeviceConnection.claimInterface(mUsbInterface, true)) {
                            message = "connect success2";
                            isSuccess = true;
                            //进行逻辑读取数据
                            byte[] req = new byte[64];
                            byte[] dst = new byte[16];
                            byte[] dst2 = new byte[12];
                            Arrays.fill(req, (byte) 0);
                            req[1 - 1] = (byte) 0xaa;
                            req[2 - 1] = 0x55;
                            req[3 - 1] = 0x04;
                            req[4 - 1] = 0x10;
                            req[15 - 1] = 0x55;
                            req[16 - 1] = (byte) 0xaa;

                            int ret = -1;
                            int maxTimes = 3;
                            int retime = 0;
                            while ((ret = mDeviceConnection.bulkTransfer(outEndpoint, req, req.length, 100)) < 0) {
                                retime++;
                                if (retime > maxTimes) {
                                    break;
                                }
                            }
                            msg.append("sen data" + ret);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ret = -1;
                            retime = 0;
                            byte[] res = new byte[64];
                            Arrays.fill(res, (byte) 0);

                            ret = mDeviceConnection.bulkTransfer(inEndpoint, res, inEndpoint.getMaxPacketSize(), 30);

                            msg.append("recv:" + ret + "(" + retime + "**");
                            msg.append(Constants.print(res, 64)).append("@@");

                           /* if (!AESOperation.aes128ofbDecrypt(res, 4, dst2)) {
                                msg.append("解密失败");
                            }*/
                            System.arraycopy(res, 4, dst2, 0, 12);

                            try {
                                msg.append(Constants.print(dst2, 12)).append("###");
                                // 首字节固定为0x11
                                // 获取本次通电时间、总通电时间和通电次数

                                Constants.addDebugMsg("*****");
                                Constants.addDebugMsg("1:" + ((dst2[1] & 0xff) << 16));
                                Constants.addDebugMsg("2:" + ((dst2[2] & 0xff) << 8));
                                Constants.addDebugMsg("3:" + ((dst2[3] & 0xff)));
                                Constants.addDebugMsg("*****");
                                int[] nums = new int[]{
                                        ((dst2[1] & 0xff) << 16) | ((dst2[2] & 0xff) << 8) | (dst2[3] & 0xff),
                                        (((dst2[6] & 0xff)) << 8) | (dst2[7] & 0xff),
                                        (((dst2[10] & 0xff)) << 8) | (dst2[11] & 0xff)};

                                //取挂飞次数
                                System.arraycopy(res, 16, dst2, 0, 12);
                                int zgfcs = (((dst2[10] & 0xff)) << 8) | (dst2[11] & 0xff);
                                // 获取弹号和导引头号
                                //AESOperation.aes128ofbDecrypt(res, 28, dst);
                                System.arraycopy(res, 28, dst, 0, 16);
                                msg.append(Constants.print(dst, 16)).append("###");
                                String mn = new String(dst, "GBK").trim();
                                mn = new String(mn.getBytes(StandardCharsets.UTF_8));
                                //AESOperation.aes128ofbDecrypt(res, 44, dst);
                                System.arraycopy(res, 44, dst, 0, 16);
                                msg.append(Constants.print(dst, 16)).append("###");
                                String tn = new String(dst, "GBK").trim();
                                tn = new String(tn.getBytes(StandardCharsets.UTF_8));
                                DecimalFormat nf = new DecimalFormat("0000.000");
                                msg.append("弹    号: " + mn + "\n");
                                msg.append("导 引 头: " + tn + "\n");
                                msg.append("本次通电: %d秒" + nums[0] + "\n");
                                msg.append("总 通 电: " + nums[1] / 3600 + "," + (nums[1] % 3600) / 60 + "," + nums[1] % 60);
                                msg.append("通电次数:" + nums[2]);
                                msg.append("挂飞次数:" + zgfcs);
                                Log.d(TAG, "get014JsQ3Response: msg:" + msg.toString());
                                JSONObject jo = new JSONObject();
                                //Todo
                                jo.put("dybh", mn);
                                jo.put("dyt", tn);
                                jo.put("bctdsj", nums[1]);
                                jo.put("ztdsjs", nums[0] / 3600);
                                jo.put("ztdsjf", (nums[0] % 3600) / 60);
                                jo.put("ztdsjm", nums[0] % 60);
                                jo.put("tdcs", nums[2]);
                                jo.put("zgfcs", zgfcs);
                                jo.put("bctdsjs", nums[1] / 3600);
                                jo.put("bctdsjf", (nums[1] % 3600) / 60);
                                jo.put("bctdsjm", nums[1] % 60);
                                Constants.addDebugMsg("msg:" + msg.toString());
                                msg.setLength(0);
                                msg.append(jo.toJSONString());
                                Log.d(TAG, "get014JsQ3Response: jo:\n" + jo.toJSONString());
                                Constants.addDebugMsg(jo.toJSONString());
                                mDeviceConnection.close();
                                return new Pair<>(true, jo.toJSONString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Constants.addDebugMsg(Constants.printError(e, "数据解析失败！"));
                                return new Pair<>(false, "Data parsing failed");
                            }
                        } else {
                            mDeviceConnection.close();
                        }
                    } else {
                        msg.append("no permission");

                        Log.d(TAG, "get014JsQ3Response: no permission");
                        PendingIntent pi = PendingIntent.getBroadcast(ActivityUtils.getTopActivity(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                        mUsbManager.requestPermission(device, pi);
                        return new Pair<>(false, "no permission");
                    }
                    break;
                }
            }
        } else {
            msg.append("no devide ");
            Log.d(TAG, "get014JsQ3Response: no devide ");
            return new Pair<>(false, "no device");
        }
        return new Pair<>(false, "no data");
    }
}
