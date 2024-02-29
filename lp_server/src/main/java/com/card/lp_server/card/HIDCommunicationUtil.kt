package com.card.lp_server.card

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.mAppContext
import com.card.lp_server.utils.logD


class HIDCommunicationUtil private constructor() {

    private val usbManager: UsbManager =
        mAppContext.getSystemService(Context.USB_SERVICE) as UsbManager

    private val permissionIntent: PendingIntent = PendingIntent.getBroadcast(
        mAppContext,
        0,
        Intent(ACTION_USB_PERMISSION),
        0
    )
    private var usbDevice: UsbDevice? = null
    private var usbConnection: UsbDeviceConnection? = null
    private var connectionListener: ConnectionListener = DefaultConnectionListener()
    private var vendorId: Int = 6790
    private var productId: Int = 58409
     var isConnect: Boolean = false

    init {
        registerUSBReceiver()
    }

    private fun registerUSBReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        mAppContext.registerReceiver(USBReceiver(), filter)
    }

    fun setConnectionListener(listener: ConnectionListener) {
        connectionListener = listener
    }

    fun findAndOpenHIDDevice(vendorId: Int = 6790, productId: Int = 58409): Boolean {
        this.vendorId = vendorId
        this.productId = productId
        usbDevice = findHIDDevice()
        if (usbDevice != null) {
            if (usbManager.hasPermission(usbDevice)) {
                return openUSBConnection(usbDevice!!)
            } else {
                requestUSBPermission(usbDevice!!)
            }
        } else {
            connectionListener.onConnectionFailed("HID device not found")
        }
        return false
    }

    private fun findHIDDevice(): UsbDevice? {
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (device in deviceList.values) {
            if (device.vendorId == vendorId && device.productId == productId) {
                return device
            }
        }
        return null
    }

    private fun requestUSBPermission(usbDevice: UsbDevice) {
        usbManager.requestPermission(usbDevice, permissionIntent)
    }

    private inner class USBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    LogUtils.d(device)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            openUSBConnection(device)
                        }
                    } else {
                        connectionListener.onConnectionFailed("USB permission denied")
                    }
                }
            }
        }
    }

    private fun openUSBConnection(usbDevice: UsbDevice): Boolean {
        usbConnection = usbManager.openDevice(usbDevice)
        if (usbConnection != null) {
            isConnect = true
            connectionListener.onConnectedSuccess()
        } else {
            isConnect = false
            connectionListener.onConnectionFailed("Failed to open USB connection")
        }
        return isConnect
    }

    fun closeUSBConnection() {
        isConnect = false
        if (usbConnection != null) {
            usbConnection?.close()
            connectionListener.onDisconnected()
        }
    }

    fun readFromHID(buffer: ByteArray): Boolean {
        if (ensureHIDConnection()) {
            val endpoint = findHIDEndpoint(UsbConstants.USB_DIR_IN)
            if (endpoint != null) {
                val bytesRead =
                    usbConnection?.bulkTransfer(endpoint, buffer, buffer.size, TIMEOUT) ?: 0
                if (bytesRead > 0) {
                    return true
                } else {
                    connectionListener.onReadFailed("Read from HID failed")
                }
            } else {
                connectionListener.onReadFailed("Input endpoint not found")
            }
        }
        return false
    }

    fun writeToHID(buffer: ByteArray): Boolean {
        if (ensureHIDConnection()) {
            val endpoint = findHIDEndpoint(UsbConstants.USB_DIR_OUT)
            if (endpoint != null) {
                val bytesWritten =
                    usbConnection?.bulkTransfer(endpoint, buffer, buffer.size, TIMEOUT) ?: 0
                if (bytesWritten > 0) {
                    return true
                } else {
                    connectionListener.onWriteFailed("Write to HID failed")
                }
            } else {
                connectionListener.onWriteFailed("Output endpoint not found")
            }
        }
        return false
    }


    private fun ensureHIDConnection(): Boolean {
        if (usbConnection == null || !usbConnection!!.claimInterface(
                usbDevice?.getInterface(0),
                true
            )
        ) {
            // If connection is not open or interface claim fails, attempt to reconnect
            return reconnectToHID()
        }
        return true
    }

    private fun reconnectToHID(): Boolean {
        closeUSBConnection()
        val findAndOpenHIDDevice = findAndOpenHIDDevice(vendorId, productId)
        return usbConnection != null && findAndOpenHIDDevice
    }

    private fun findHIDEndpoint(direction: Int): UsbEndpoint? {
        if (usbDevice != null) {
            val endpointCount = usbDevice?.getInterface(0)?.endpointCount ?: 0
            for (i in 0 until endpointCount) {
                val endpoint = usbDevice?.getInterface(0)?.getEndpoint(i)
                if (
                    endpoint?.type == UsbConstants.USB_ENDPOINT_XFER_INT &&
                    endpoint.direction == direction
                ) {
                    logD(endpoint)
                    return endpoint
                }
            }
        }
        isConnect = false
        return null
    }

    companion object {
        val ACTION_USB_PERMISSION: String = "${AppUtils.getAppPackageName()}.USB_PERMISSION"
        private const val TIMEOUT = 1000 // 5 seconds timeout
        val instance: HIDCommunicationUtil by lazy {
            val hidCommunicationUtil = HIDCommunicationUtil()
            hidCommunicationUtil.findAndOpenHIDDevice()
            hidCommunicationUtil
        }
    }
}

interface ConnectionListener {

    fun onConnectedSuccess()

    fun onDisconnected()

    fun onConnectionFailed(message: String)


    fun onReadFailed(message: String)

    fun onWriteFailed(message: String)
}

class DefaultConnectionListener : ConnectionListener {
    private val TAG = "HIDCommunicationUtil"
    override fun onConnectedSuccess() {
        Log.d(TAG, "onConnectedSuccess: ")
    }

    override fun onDisconnected() {
        // Default implementation if not overridden
        Log.d(TAG, "onDisconnected: ")
    }

    override fun onConnectionFailed(message: String) {
        // Default implementation if not overridden
        Log.d(TAG, "onConnectionFailed:$message ")
    }

    override fun onReadFailed(message: String) {
        // Default implementation if not overridden
        Log.d(TAG, "onReadFailed: $message")
    }


    override fun onWriteFailed(message: String) {
        // Default implementation if not overridden
        Log.d(TAG, "onWriteFailed: $message")
    }
}
