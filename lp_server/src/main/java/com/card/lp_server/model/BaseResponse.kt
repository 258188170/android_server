package com.card.lp_server.model

data class BaseResponse<T>(var code: Int, var data: T?, var msg: String?)


