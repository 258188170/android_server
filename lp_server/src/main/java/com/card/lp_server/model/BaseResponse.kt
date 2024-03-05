package com.card.lp_server.model

import androidx.annotation.Keep

@Keep
data class BaseResponse<T>(var code: Int, var data: T?, var msg: String?)


