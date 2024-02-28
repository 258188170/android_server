package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.utils.responseJsonStringFail
import com.card.lp_server.utils.responseJsonStringSuccess
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
        return session?.let { dealWith(it) } ?: responseJsonStringFail()
    }

    private fun dealWith(session: IHTTPSession): Response {
        LogUtils.d("请求头：${session.headers}")
        LogUtils.d("请求路径 uri：${session.uri} -->> 请求方式 method：${session.method}")
        // 使用工厂创建相应的策略
        val requestHandlerStrategy = requestHandlerFactory.createHandler(session.method)

        return requestHandlerStrategy.handleRequest(session) ?: responseJsonStringFail(
        )
    }
}
