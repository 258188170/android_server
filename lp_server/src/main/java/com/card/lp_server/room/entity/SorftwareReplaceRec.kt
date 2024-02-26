package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//软件变更记录
@Entity
data class SorftwareReplaceRec(

    @PrimaryKey val uid: Int,
    // 编号
    var dyNumber: String,
    var serialNumber: String? = null,
    var sftName: String? = null,
    var replaceDate: String? = null,
    var beforeVersion: String? = null,
    var replaceVersion: String? = null,

    // 软件变更原因
    var replaceBasis: String? = null,

    // 实施单位
    var impDept: String? = null,
    var oprator: String? = null
) 