package com.card.lp_server.utils

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.card.HIDCommunicationUtil
import fi.iki.elonen.NanoHTTPD

const val FILE_NAME = "fileName"


fun <T> handleResponse(action: () -> T): NanoHTTPD.Response {
    return try {
        val findAndOpenHIDDevice = HIDCommunicationUtil.instance.findAndOpenHIDDevice()
        if (findAndOpenHIDDevice) {
            val action1 = action()
            action1.responseJsonStringSuccess()
        } else {
            responseJsonStringFail("设备未连接,请重试")
        }
    } catch (e: Exception) {
        responseJsonStringFail(e.message)
    }
}

fun stringConvertToList(listFiles: ByteArray?): List<String> {
    if (listFiles == null) {
        return emptyList()
    }
    return try {
        val hexString = ConvertUtils.bytes2HexString(listFiles)
            .replace("0A00", "0A")
            .substringBeforeLast("0A00")

        val byteData = ConvertUtils.hexString2Bytes(hexString)
        val stringData = String(byteData)
        val resultList = stringData.split("\n")
        resultList
    } catch (e: Exception) {
        emptyList()
    }
}


fun NanoHTTPD.IHTTPSession.getQueryParams(): Map<String, List<String>> {
    // 获取GET参数
    return this.parameters
}

fun NanoHTTPD.IHTTPSession.getPostParams(): Map<String, String> {
    // 获取POST参数
    val postParams = mutableMapOf<String, String>()
    try {
        this.parseBody(postParams)
    } catch (e: Exception) {
        // 处理解析异常
        e.printStackTrace()
    }
    return postParams
}