package com.card.lp_server.server

import fi.iki.elonen.NanoHTTPD

// 策略实现1: 处理GET请求
class GetRequestHandler : RequestHandlerStrategy {
    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        return when (uri) {
            "api/home" -> NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")
            "api/about" -> NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")
            else -> null
        }
    }
}