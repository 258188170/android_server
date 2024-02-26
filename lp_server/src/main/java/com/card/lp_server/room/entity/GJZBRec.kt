package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
//GJ值班记录
@Entity
data class GJZBRec(
    @PrimaryKey val uid: Int,
    // 编号
    var dyNumber: String,
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