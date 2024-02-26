package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//技术通报
@Entity
data class TecReportImpRec(
    @PrimaryKey val uid: Int,
    // 编号
    var dyNumber: String,
    // 完成日期
    var finishDate: String? = null,

    // 报告编号
    var reportNumber: String? = null,

    // 报告名称
    var reportName: String? = null,

    // 实施单位
    var impDept: String? = null,

    // 操作人员
    var optPerson: String? = null,

    // 备注
    var memo: String? = null
)