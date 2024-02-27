package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class CodeUpRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,

    var serialNumber: String? = null,

    // 加注类型
    var upType: String? = null,
    var upDate: String? = null,

    // 有效日期
    var lmtDate: String? = null,

    // 加注人
    var optPerson: String? = null,

    // 文件版本号
    var fileVersion: String? = null
)