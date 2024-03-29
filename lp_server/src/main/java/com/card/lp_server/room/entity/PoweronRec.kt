package com.card.lp_server.room.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
//通电时间
@Entity
@Keep
data class PoweronRec(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String?=null,
    var pwDate: String? = null,

    // 地面通电时间 HH:mm。
    var dmPwTime: String? = null,

    // gf通电时间
    var drgfPwTime: String? = null,

    // 总通电时间
    var totalPwTime: String? = null,

    // 当日起落架次
    var todayUpAndDown: Int? = null,

    // 累计起落架次
    var allUpAndDown: Int? = null,

    var recPerson: String? = null,
    var memo: String? = null
)