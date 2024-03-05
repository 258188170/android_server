package com.card.lp_server.room.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

//技术通报
@Entity
@Keep
data class TecReportImpRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String? = null,
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