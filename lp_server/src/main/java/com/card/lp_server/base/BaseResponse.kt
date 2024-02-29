package com.card.lp_server.base

data class BaseResponse<T>(var success: Boolean, var data: T?, var msg: String?)


