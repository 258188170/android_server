package com.card.lp_server.server

import android.util.Log
import com.card.lp_server.utils.responseJsonStringFail
import fi.iki.elonen.NanoHTTPD


class LServer(port: Int = 9988) : NanoHTTPD(port) {
    companion object {
        private const val TAG = "LServer"
    }

    private val requestHandlerFactory by lazy {
        RequestHandlerFactory()
    }

    override fun serve(session: IHTTPSession?): Response {
        Log.d(TAG, "线程 ${Thread.currentThread().name}")
        return session?.let { dealWith(it) } ?: responseJsonStringFail(
            msg = "session is null"
        )
    }

    private fun dealWith(session: IHTTPSession): Response {
        Log.d(TAG, "dealWith: \"请求头：${session.headers}\"")
        Log.d(
            TAG,
            "dealWith: \"请求路径 uri：${session.uri} -->> 请求方式 method：${session.method}\""
        )
        // 使用工厂创建相应的策略
        val requestHandlerStrategy = requestHandlerFactory.createHandler(session.method)

        return requestHandlerStrategy.handleRequest(session) ?: responseJsonStringFail(
            null
        )
    }
}
