package com.card.lp_server.card.device.util;


import static android.hardware.usb.UsbManager.EXTRA_PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.card.lp_server.card.device.model.Pair;

import java.util.HashMap;

public class HidUtils {
    private static final String TAG = "UsbHidUtil";
    public static final String ACTION_USB_PERMISSION = "com.example.USB_PERMISSION";
    private static UsbManager usbManager;
    private static UsbDeviceConnection connection;
    private static UsbInterface usbInterface;
    private static UsbEndpoint endpointIn;
    private static UsbEndpoint endpointOut;
    private static final int DEFAULT_TIMEOUT = 1000;
    public static boolean isConnet = false;

    public static Pair<Boolean, String> connect(int vendorId, int productId) {
        usbManager = (UsbManager) ActivityUtils.getTopActivity().getApplication().getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d(TAG, "connect: deviceList.size:" + deviceList.size());
        UsbDevice device = null;
        for (UsbDevice usbDevice : deviceList.values()) {
            Log.d(TAG, "connect: usbDevice.getVendorId()" + usbDevice.getVendorId() + "usbDevice.getProductId()" + usbDevice.getProductId());
            if (usbDevice.getVendorId() == vendorId && usbDevice.getProductId() == productId) {
                device = usbDevice;
                break;
            }
        }
        if (device == null) {
            Log.d(TAG, "connect: device not found");
            return new Pair<>(false, "no device");
        }
        if (!usbManager.hasPermission(device)) {

            PendingIntent pi = PendingIntent.getBroadcast(ActivityUtils.getTopActivity(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            usbManager.requestPermission(device, pi);
            return new Pair<>(false, "no permission");
        }
        return openDevice(device);
    }

    public static void disconnect() {
        closeDevice();
    }

    public static Pair<Boolean, String> openDevice(UsbDevice device) {
        usbInterface = device.getInterface(0);
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
                if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                    endpointIn = ep;
                } else if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    endpointOut = ep;
                }
            }
        }
        Log.d(TAG, "openDevice: " + endpointIn + " ---" + endpointOut);
        connection = usbManager.openDevice(device);
        if (connection != null && connection.claimInterface(usbInterface, true)) {
            Log.d(TAG, "openDevice: 连接成功");
            isConnet = true;
            return new Pair<>(true, "connect success!");
            // 连接成功
        } else {
            // 连接失败
            Log.d(TAG, "openDevice: 连接失败");
            isConnet = false;
            return new Pair<>(true, "connect fail!");
        }
    }


    public static int write(byte[] buffer) {
//        Log.d(TAG, "write: " + Arrays.toString(buffer) + " 长度 " + buffer.length);
        return connection != null ? connection.bulkTransfer(endpointOut, buffer, buffer.length, DEFAULT_TIMEOUT) : 0;
    }

    public static int read(byte[] buffer) {
//        Log.d(TAG, "read: " + Arrays.toString(buffer) + " 长度 " + buffer.length);
        return connection != null ? connection.bulkTransfer(endpointIn, buffer, buffer.length, DEFAULT_TIMEOUT) : 0;
    }

    public static boolean closeDevice() {
        try {
            if (connection != null) {
                connection.releaseInterface(usbInterface);
                connection.close();
            }
            isConnet = false;
            connection = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Log.d(TAG, "onReceive: 用户授予了USB权限");
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device != null) {
                            // 用户授予了USB权限
                            openDevice(device);
                        } else {
                            // 用户拒绝了USB权限
                            LogUtils.d("device not found");
                        }
                    } else {
                        Log.d(TAG, "onReceive: " + " 用户拒绝了USB权限");
                    }
                }
            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static void registerUsbReceiver(Context context) {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(ACTION_USB_PERMISSION);
        usbFilter.addAction(EXTRA_PERMISSION_GRANTED);
        context.registerReceiver(usbReceiver, usbFilter);
    }

    public static void unregisterUsbReceiver(Context context) {
        context.unregisterReceiver(usbReceiver);
    }


}
