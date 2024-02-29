package com.card.lp_server.utils

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.base.IBaseRepository
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.mAppContainer
import com.card.lp_server.model.Types
import fi.iki.elonen.NanoHTTPD
import java.net.URLDecoder

const val FILE_NAME = "fileName"
const val TYPE_NUMBER = "typeNumber"


fun <T> handleResponse(action: () -> T): NanoHTTPD.Response {
    return try {
        val findAndOpenHIDDevice = HIDCommunicationUtil.instance.isConnect
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

fun String?.getType(): String {
    try {
        return when (this?.toInt()) {
            2 -> Types.CODE_UP_REC.value
            3 -> Types.EQU_MATCH.value
            4 -> Types.EQU_REPLACE_REC.value
            5 -> Types.GASUP_REC.value
            6 -> Types.GJZB_REC.value
            7 -> Types.HANDOVER_REC.value
            8 -> Types.MT_REC.value
            9 -> Types.POWERON_REC.value
            10 -> Types.REPAIR_REC.value
            11 -> Types.SORFTWARE_REC.value
            12 -> Types.TECREPORTIMP_REC.value
            else -> ""
        }
    } catch (e: Exception) {
        return ""
    }
}

fun NanoHTTPD.IHTTPSession.getPostParams(): String? {
    // 获取POST参数
    val postParams = mutableMapOf<String, String>()
    try {
        this.parseBody(postParams)
        logD("POST 请求参数： $postParams")
        val postData = postParams["postData"]?.takeIf { it.isNotEmpty() }?.let {
            URLDecoder.decode(it, "UTF-8")
        }
        logD("请求的 JSON 数据:$postData")
        return postData
    } catch (e: Exception) {
        // 处理解析异常
        e.printStackTrace()
    }
    return null
}

