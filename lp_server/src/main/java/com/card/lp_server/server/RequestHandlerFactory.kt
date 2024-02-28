package com.card.lp_server.server

import fi.iki.elonen.NanoHTTPD

// 工厂模式用于创建策略实例
class RequestHandlerFactory {
    fun createHandler(method: NanoHTTPD.Method): RequestHandlerStrategy {
        return when (method) {
            NanoHTTPD.Method.GET -> GetRequestHandler()
            NanoHTTPD.Method.POST -> PostRequestHandler()
            else -> throw IllegalArgumentException("Unsupported HTTP method")
        }
    }
}