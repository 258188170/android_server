package com.card.lp_server.base

data class BaseResponse<T>(var code: Int, var data: T?, var msg: String?)


