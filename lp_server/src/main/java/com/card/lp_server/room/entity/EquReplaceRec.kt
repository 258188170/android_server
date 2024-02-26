package com.card.lp_server.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
//设备更换记录
@Entity
data class EquReplaceRec(
    @PrimaryKey val uid: Int,
    // 编号
    var dyNumber: String,

    // 序号
    var serialNumber: String? = null,

    // 设备名称
    var devName: String? = null,

    // 拆卸日期
    var disDate: String? = null,

    // 拆卸设备编号
    var disDevNumber: String? = null,

    // 安装日期
    var installDate: String? = null,

    // 安装设备编号
    var installDevNumber: String? = null,

    // 更换原因
    var replaceReson: String? = null,

    // 操作者
    var operator: String? = null,

    // 备注
    var memo: String? = null,

    // 文件版本号
    var fileVersion: String? = null
) 