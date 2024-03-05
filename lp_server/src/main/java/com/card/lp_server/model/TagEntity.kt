package com.card.lp_server.model

import androidx.annotation.Keep

@Keep
class TagEntity(
    var fileName: String? = null,
    val data: ByteArray? = null
)
