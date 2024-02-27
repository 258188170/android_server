package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class GasUpRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    var recDate: String? = null,

    // 汽油分析报告号
    var gasReportNumber: String? = null,

    // 燃油型号
    var gasModel: String? = null,

    // 空气温度
    var airTemperature: Double? = null,

    // 燃油温度
    var gasTemperature: Double? = null,

    // 环境温度
    var envTemperature: Double? = null,

    // 燃油操作类别 :加注、放泄
    var gasOptType: String? = null,

    // 燃油加注量
    var upQuantity: Double? = null,

    // 燃油操作人
    var gasOptPerson: String? = null,

    // 滑油操作类别
    var lubOptType: String? = null,

    // 滑油操作人员
    var lubOptPerson: String? = null,

    // 滑油加注量
    var lubUpQuantity: Double? = null,

    // 备注
    var memo: String? = null,

    // 文件版本号
    var fileVersion: String? = null
)