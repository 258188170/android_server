package com.card.lp_server.base

class BaseResponse<T>(var code: Int, var data: T?, var msg: String)