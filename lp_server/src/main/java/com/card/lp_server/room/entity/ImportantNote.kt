package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ImportantNote(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    // 记事日期
    var noteDate: String? = null,

    // 记事内容
    var noteContent: String? = null,

    // 文件版本号
    var fileVersion: String? = null
)