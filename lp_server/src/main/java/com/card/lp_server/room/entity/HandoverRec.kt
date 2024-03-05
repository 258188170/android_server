package com.card.lp_server.room.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
//交接记录
@Entity
@Keep
data class HandoverRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    // 移交日期
    var hdDate: String? = null,

    // 移交单位
    var hdDept: String? = null,

    // 人员
    // 移交人员
    var hdPerson: String? = null,

    // 依据
    var hdBasics: String? = null,

    // 接收日期
    var acptDate: String? = null,

    // 接收部门
    var acptDept: String? = null,

    // 接收人员
    var acptPerson: String? = null,

    // 文件版本号
    var fileVersion: String? = null
)