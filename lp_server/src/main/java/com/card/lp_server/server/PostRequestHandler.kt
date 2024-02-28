package com.card.lp_server.server

import fi.iki.elonen.NanoHTTPD

// 策略实现2: 处理POST请求
class PostRequestHandler : RequestHandlerStrategy {

    private val handlers = mapOf(
        "/home" to ::handleHome,
        "/about" to ::handleAbout
        // Add more URL mappings as needed
    )

    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        val handler = handlers[uri]
        return handler?.invoke(session)
    }

    private fun handleHome(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")
    }

    private fun handleAbout(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")
    }

    // Add more handler functions for other URLs if needed
}

