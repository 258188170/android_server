package com.card.lp_server.model

import androidx.annotation.Keep

@Keep
data class PostParams(val jsq: String?, val number: String?)
@Keep
data class PostParamsJD(
    val jsq: String?,
    val jwdztdsj: Int?,
    val jwdbcsj: Int?,
    val jwdztdcs: Int?,
    val zbdztdsj: Int?,
    val zbdbcsj: Int?,
    val zbdztdcs: Int?,
)
