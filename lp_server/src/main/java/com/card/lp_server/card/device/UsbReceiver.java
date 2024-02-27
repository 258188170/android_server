package com.card.lp_server.card.device;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if (action != null) {
//            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);//获取附件设备
//                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {//判断设备是否有usb
//                    if (usbDevice != null) {
//                        LonbestCard.getInstance().close();
//                        LonbestCard.getInstance().connect();
//                        ToastUtils.showShort("usb权限开启成功");
//                    } else {
//                        ToastUtils.showShort("USB设备连接异常");
//                    }
//                } else {
//                    //打开权限失败
//                    Log.d(TAG, "onReceive: no permission");
////                    ToastUtils.showShort("USB设备连接异常,请重试");
//                }
//            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                // USB设备已断开
//                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if (device != null) {
//                    LonbestCard.getInstance().close();
//                    Log.d(TAG, "USB设备已断开: " + device.getDeviceName());
//                }
//            }
//        }
    }
}
