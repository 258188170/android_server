package com.card.lp_server.room.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
//GJ值班记录
@Entity
@Keep
data class GJZBRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    // 开始日期
    var startDate: String? = null,

    // 结束日期
    var endDate: String? = null,

    // 值班天数
    var zbDays: Int? = null,

    // 累计值班天数
    var totalZbDays: Int? = null,

    // 记录人
    var recPerson: String? = null,

    // 文件版本号
    var fileVersion: String? = null
)