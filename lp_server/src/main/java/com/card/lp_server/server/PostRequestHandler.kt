package com.card.lp_server.server

import fi.iki.elonen.NanoHTTPD

// 策略实现2: 处理POST请求
class PostRequestHandler : RequestHandlerStrategy {
    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        return when (uri) {
            "/home" -> NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")
            "/about" -> NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")
            else -> null
        }
    }
}
