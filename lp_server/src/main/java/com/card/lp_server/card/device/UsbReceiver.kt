package com.card.lp_server.card.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log

class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null) {
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                // USB设备已断开
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                if (device != null) {
                    Log.d(TAG, "USB设备已断开: " + device.deviceName)
                }
            }
        }
    }

    companion object {
        private const val TAG = "UsbReceiver"
    }
}
