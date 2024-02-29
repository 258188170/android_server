package com.card.lp_server.utils

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.base.BaseResponse
import fi.iki.elonen.NanoHTTPD


fun <T> responseJsonStringSuccess(
    data: T?,
    msg: String = "操作成功",
): NanoHTTPD.Response {
    val response = BaseResponse(200, data, msg)
    Log.d(TAG, "responseJsonStringSuccess: $data")
    return NanoHTTPD.newFixedLengthResponse(GsonUtils.toJson(response))//返回对应的响应体Response
}

fun responseJsonStringFail(
    msg: String? = "操作失败,请重试",
): NanoHTTPD.Response {
    val response = BaseResponse(500,null, msg)
    Log.d(TAG, "responseJsonStringFail: ")
    return NanoHTTPD.newFixedLengthResponse(GsonUtils.toJson(response))//返回对应的响应体Response
}