package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ImportantNote(
    @PrimaryKey val uid: Int,
    // 编号
    var dyNumber: String,
    // 记事日期
    var noteDate: String? = null,

    // 记事内容
    var noteContent: String? = null,

    // 文件版本号
    var fileVersion: String? = null
)