package com.card.lp_server.room.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
@Keep
data class MtRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    var mtDate: String? = null,
    var mtContent: String? = null,
    var mtResult: String? = null,
    var mtPerson: String? = null,
    var memo: String? = null,

    // 文件版本号
    var fileVersion: String? = null
) 