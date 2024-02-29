package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// 设备装备配套表
@Entity
data class EquMatch(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String? = null,
    // 编号
    var serialNumber: String? = "ZB-A",
    // 名称
    var equName: String? = "装备A",

    // 代号
    var codeName: String? = "ZB001",

    // 数量
    var quantity: Int? = 10,

    // 生产商
    var manufacturer: String? = "A公司",

    // 设备编号或者软件版本号
    var devId: String? = null,

    // 是否更换、变更
    var replace: Boolean? = false,

    // 备注
    var memo: String? = "无",
    // 文件版本号
    var fileVersion: Int? = 0
)