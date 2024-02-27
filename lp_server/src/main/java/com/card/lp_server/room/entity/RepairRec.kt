package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class RepairRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    // 修理单位
    var repairDept: String? = null,

    // 检修、翻修、延寿
    var repairType: String? = null,

    // 修理依据
    var repairBasis: String? = null,

    // 修理内容
    var repairContent: String? = null,

    // 修理结论
    var repairResult: String? = null
) 