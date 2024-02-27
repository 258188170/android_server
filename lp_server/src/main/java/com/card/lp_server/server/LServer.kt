package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.utils.responseJsonString
import fi.iki.elonen.NanoHTTPD
import java.net.URLDecoder


class LServer(port: Int = 9988) : NanoHTTPD(port) {
    companion object {
        private const val TAG = "LServer"
    }

    private val requestHandlerFactory by lazy {
        RequestHandlerFactory()
    }

    override fun serve(session: IHTTPSession?): Response {
        Log.d(TAG, "线程 ${Thread.currentThread().name}")
        return session?.let { dealWith(it) } ?: responseJsonString(404, "", "请求不受支持!")
    }

    private fun dealWith(session: IHTTPSession): Response {
        LogUtils.d("请求头：${session.headers}")
        LogUtils.d("请求路径 uri：${session.uri} -->> 请求方式 method：${session.method}")

        val paramsMap = mutableMapOf<String, String>()
        session.parseBody(paramsMap)
        LogUtils.d("请求参数：${paramsMap.toMap()}")

        val postData = paramsMap["postData"]?.takeIf { it.isNotEmpty() }?.let {
            URLDecoder.decode(it, "UTF-8")
        }
        LogUtils.d("请求的 JSON 数据：$postData")
        // 使用工厂创建相应的策略
        val requestHandlerStrategy = requestHandlerFactory.createHandler(session.method)

        return requestHandlerStrategy.handleRequest(session) ?: responseJsonString(
            404,
            "",
            "请求不受支持!"
        )
    }
}
