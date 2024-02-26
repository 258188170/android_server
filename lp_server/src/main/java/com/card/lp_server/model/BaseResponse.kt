package com.card.lp_server.model

class BaseResponse<T>(var code: Int, var data: T?, var msg: String)