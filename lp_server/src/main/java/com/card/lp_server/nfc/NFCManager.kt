package com.card.lp_server.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.mAppContext
import com.card.lp_server.model.RequestApdu
import com.card.lp_server.model.ResponseApdu
import com.card.lp_server.utils.encrypt3DES

private const val TAG = "NFCManager"

object NFCManager {

    private val nfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(mAppContext)
    }

    private const val READER_FLAGS = (NfcAdapter.FLAG_READER_NFC_A
            or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS)

    private fun isAvailable(): Boolean {
        return nfcAdapter.isEnabled
    }

    fun onResume(activity: Activity) {
        val pendingIntent = PendingIntent.getActivity(
            activity, 0, Intent(activity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        NfcAdapter.getDefaultAdapter(activity)
            ?.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    fun onPause(activity: Activity) {
        NfcAdapter.getDefaultAdapter(activity)?.disableForegroundDispatch(activity)
    }

    fun startSession(readerCallback: (IsoDep) -> Unit) {
        if (!isAvailable()) {
            Log.d(TAG, "startSession:  nfc not found || isEnabled is false")
            return
        }
        Log.d(TAG, "startSession: isAvailable is true")
        nfcAdapter.enableReaderMode(ActivityUtils.getTopActivity(), NfcAdapter.ReaderCallback {
            Log.d(TAG, "startSession: ${it.id}")
            val isoDep = IsoDep.get(it)
            if (isoDep != null) {
                isoDep.connect()
                readerCallback.invoke(isoDep)
                isoDep.close()
            } else
                Log.d(TAG, "startSession: isoDep is null!!!")
        }, READER_FLAGS, null)
    }

    fun stopSession() {
        nfcAdapter.disableReaderMode(ActivityUtils.getTopActivity())
    }


}

fun IsoDep.nfcTransceive(requestApdu: RequestApdu): ResponseApdu {

    Log.d(
        TAG,
        "nfcTransceive: requestApdu-->${ConvertUtils.bytes2HexString(requestApdu.toBytes())}"
    )
    val transceive = transceive(requestApdu.toBytes())
    Log.d(TAG, "nfcTransceive: ${ConvertUtils.bytes2HexString(transceive)}")
    return transceive.analyze()
}

fun ByteArray.analyze(): ResponseApdu {
    val responseApdu = ResponseApdu()

    if (size == 2) {
        responseApdu.SW = this
    } else if (size > 2) {
        responseApdu.SW = copyOfRange(size - 2, size)
        responseApdu.body = copyOfRange(0, size - 2)
    }
    return responseApdu
}

fun IsoDep.nfcAuthIsSuccess(
    dir: String = "df4c",
    file: String = "ef31",
    key: String = "4C010101010101010101010101010143"
): Boolean {

    ///获取 8 字卡号
    val nfcTransceive = nfcTransceive(RequestApdu(INS = 0xb0, P1 = 0x81, P2 = 0x02, LE = 0x08))

    if (!nfcTransceive.checkSW()) {
        Log.d(TAG, "nfcAuthIsSuccess: 获取卡号失败")
        return false
    }
    //选择目录 df05
    val nfcTransceive1 =
        nfcTransceive(RequestApdu(INS = 0xa4, LC = 0x02, DATA = ConvertUtils.hexString2Bytes(dir)))
    if (!nfcTransceive1.checkSW()) {
        Log.d(TAG, "nfcAuthIsSuccess: 选择目录 $dir 失败!")
        return false
    }
    //选择文件 ef31
    val nfcTransceive2 =
        nfcTransceive(RequestApdu(INS = 0xa4, LC = 0x02, DATA = ConvertUtils.hexString2Bytes(file)))
    if (!nfcTransceive2.checkSW()) {
        Log.d(TAG, "nfcAuthIsSuccess: 选择文件 $file 失败!")
        return false
    }
    //获取 8 字节随机数
    val nfcTransceive3 = nfcTransceive(
        RequestApdu(INS = 0x84, LC = 0x08)
    )
    if (!nfcTransceive3.checkSW()) {
        Log.d(TAG, "nfcAuthIsSuccess: 获取 8 字节随机数失败!")
        return false
    }
    if (nfcTransceive3.body == null || nfcTransceive.body == null)
        return false
    //3DES加密
    val tagA =
        encrypt3DES(nfcTransceive.body!!, key, nfcTransceive3.body!!)
    ///拼接密钥和随机数
    val toMutableList = tagA.toMutableList()
    toMutableList.addAll(nfcTransceive3.body!!.toMutableList())
    ///认证
    val nfcTransceive4 = nfcTransceive(
        RequestApdu(
            CLA = 0x00,
            INS = 0x82,
            P2 = 0x06,
            LC = toMutableList.size,
            DATA = toMutableList.toByteArray()
        )
    )
    Log.d(
        TAG, "认证是否成功 ${nfcTransceive4.checkSW()}"
    )
    return nfcTransceive4.checkSW()
}

fun IsoDep.nfcWrite(data: ByteArray): Boolean {

    val chunkSize = 200 // Maximum size for each packet
    val dataBytes = data.toList()

    for (offset in dataBytes.indices step chunkSize) {
        val remainingBytes = dataBytes.size - offset
        val bytesToWrite = if (remainingBytes > chunkSize) chunkSize else remainingBytes
        val chunk = dataBytes.subList(offset, offset + bytesToWrite).toByteArray()
        val write = nfcTransceive(
            RequestApdu(
                INS = 0xd6,
                P1 = intToByteArray(offset)[0].toInt(),
                P2 = intToByteArray(offset)[1].toInt(),
                LC = bytesToWrite,
                DATA = chunk
            )
        )
        if (!write.checkSW()) {
            Log.d("NFCWrite", "Write failed")
            return false
        }
        Log.d(
            TAG,
            "Sent [00b0 Offset: Length:$bytesToWrite] " +
                    "Write data: ${
                        String(
                            chunk,
                            Charsets.UTF_8
                        )
                    } Response Apdu SW: ${ConvertUtils.bytes2HexString(write.SW)}"
        )
    }
    return true
}

fun IsoDep.nfcReadData(
    offset: Int = 0,
    totalDataSize: Int
): ByteArray? {

    val chunkSize = 200
    val responseData = mutableListOf<Byte>()

    for (index in 0 until totalDataSize step chunkSize) {
        val remainingBytes = totalDataSize - index
        val bytesToRead = if (remainingBytes > chunkSize) chunkSize else remainingBytes

        val responseApdu = nfcTransceive(
            RequestApdu(
                INS = 0xb0,
                P1 = intToByteArray(index)[0].toInt(),
                P2 = intToByteArray(index)[1].toInt(),
                LC = bytesToRead
            )
        )

        if (responseApdu.body == null || !responseApdu.checkSW()) {
            Log.d("NFCReadData", "Read failed!")
            return null
        }

        Log.d(
            "NFCReadData",
            "Sent [00b0 Offset:${offset} Length:$bytesToRead] " +
                    "Read data: ${ConvertUtils.bytes2HexString(responseApdu.body)} Response Apdu SW: ${
                        ConvertUtils.bytes2HexString(
                            responseApdu.SW
                        )
                    }"
        )
        responseData.addAll(responseApdu.body!!.toMutableList())
    }

    Log.d("NFCReadData", "responseData ${responseData.size}")
    return responseData.toByteArray()
}


private fun intToByteArray(value: Int): ByteArray {
    // Ensure the value is in the range of 0x0000 to 0x7FFF
    val clampedValue = when {
        value < 0 -> 0
        value > 0x7FFF -> 0x7FFF
        else -> value
    }

    // Split the integer value into two bytes
    val byte1 = ((clampedValue shr 8) and 0x7F).toByte() // Limit the highest bit to 0x7F
    val byte2 = (clampedValue and 0xFF).toByte()
    // Create a ByteArray containing two bytes
    return byteArrayOf(byte1, byte2)
}


fun ResponseApdu.checkSW(): Boolean {
    if (this.SW?.isNotEmpty() == true) {
        val bytes2String = ConvertUtils.bytes2HexString(this.SW)
        Log.d(TAG, "checkSW: [$bytes2String]")
        return bytes2String == "9000"
    }
    return false
}

