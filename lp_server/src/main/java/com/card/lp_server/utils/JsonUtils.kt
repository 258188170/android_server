package com.card.lp_server.utils

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.base.BaseResponse
import fi.iki.elonen.NanoHTTPD


fun <T > T.responseJsonStringSuccess(
    code: Int = 200,
    msg: String = "success"
): NanoHTTPD.Response {
    val response = BaseResponse(code, this, msg)
    LogUtils.d("responseJsonString: $response")
    return NanoHTTPD.newFixedLengthResponse(GsonUtils.toJson(response))//返回对应的响应体Response
}

fun responseJsonStringFail(
    msg: String? = "设备未连接,请重试",
    code: Int = 500,
): NanoHTTPD.Response {
    val response = BaseResponse(code, null, msg)
    LogUtils.d("responseJsonString: $response")
    return NanoHTTPD.newFixedLengthResponse(GsonUtils.toJson(response))//返回对应的响应体Response
}