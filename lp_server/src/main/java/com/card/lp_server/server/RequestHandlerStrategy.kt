package com.card.lp_server.server

import fi.iki.elonen.NanoHTTPD

// 定义策略接口
interface RequestHandlerStrategy {
    fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response?
}