package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class MtRec(
    @PrimaryKey val uid: Int,
    // 编号
    var dyNumber: String,
    var mtDate: String? = null,
    var mtContent: String? = null,
    var mtResult: String? = null,
    var mtPerson: String? = null,
    var memo: String? = null,

    // 文件版本号
    var fileVersion: String? = null
) 