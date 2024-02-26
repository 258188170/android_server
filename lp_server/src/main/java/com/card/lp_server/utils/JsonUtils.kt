package com.card.lp_server.utils

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.model.BaseResponse
import fi.iki.elonen.NanoHTTPD


 fun <T : Any> responseJsonString(code: Int, data: T, msg: String): NanoHTTPD.Response {
    val response = BaseResponse<T>(code, data, msg)
    LogUtils.d("responseJsonString: $response")
    return NanoHTTPD.newFixedLengthResponse(GsonUtils.toJson(response))//返回对应的响应体Response
}