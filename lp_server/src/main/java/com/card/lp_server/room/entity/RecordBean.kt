package com.card.lp_server.room.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/*
履历信息
 */
@Entity()
@Keep
data class RecordBean(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // 编号
    var dyNumber: String? = "dddd",

    // 型号
    var dyvModel: String? = "PL-12",

    // 质量等级
    var qualityLevel: String? = "新品",

    // 验收日期
    var acpetDate: String? = "",

    // 验收方代表
    var acptRepersent: String? = "李代表",

    // 承制单位
    var cntCmpy: String? = "A单位",

    // 交付日期
    var delDate: String? = "",

    // 验收证明熟属性j
    var wtyPeriod: Int? = 0,

    // 质保起算日期
    var wtyStartDate: String? = "",

    // 质保期
    var zbq: String? = "10",

    // 存储寿命
    var stgLife: Int? = 30,

    // 存储寿命起算日期
    var stgStartDate: String? = "",

    // 首翻期
    var fristRpPeriod: Int? = 10,

    // 总通电时间
    var totPwTime: Int? = 30,

    // 起落次数
    var upAndDowNum: Int? = 10,

    // 承制方代表
    var cntRepresent: String? = "张代表",

    // 验收证明,存储base64
    var acptCert: String? = "",

    // 基本特性部分:dt长度
    var bswxDtcd: Int? = 1800,

    // dt直径
    var bswxDtzj: Int? = 40,

    // 翼展
    var bswxDtyz: Int? = 30,

    // 尾展
    var bswxWyyz: Int? = 40,

    // 重量
    var bswxWeight: Int? = 50,

    // x-心
    var bswxXZx: Int? = 50,

    // y-
    var bswxYZx: Int? = 20,

    // 油箱容积
    var bswxYxrj: Int? = 100,

    // 特殊说明
    var specExplan: String? = "无",


    // 该版本是否和标签同步
    // 1:同步 0：未同步，具体怎么控制，还要想一下
    var syncToLabel: Int? = null,

    // 更新日期，这个日期将作为版本号的一部分
    var updateDate: String? = null,
    // 文件版本号
    var fileVersion: String? = null,
    var display: Boolean? = false,
    // 最后一次写入时间
    var lastWriteTime: Long? = System.currentTimeMillis(),
    //-------------------以下是经常变更的---------------------
    // 设备配套表
    @Ignore
    var equMatches: List<EquMatch>? = null,
    @Ignore
    // 设备更换记录
    var equReplaceRecs: List<EquReplaceRec>? = null,
    @Ignore
    // 软件变更记录
    var sftReplaceRecs: List<SorftwareReplaceRec>? = null,
    @Ignore
    // 交接记录
    var hdRecs: List<HandoverRec>? = null,
    @Ignore
    // GJ值班记录
    var gjzbRecs: List<GJZBRec>? = null,
    @Ignore
    // 通电时间
    var poweronRecs: List<PoweronRec>? = null,
    @Ignore
    // 维护记录
    var mtRecs: List<MtRec>? = null,
    @Ignore
    // 加汽油记录
    var gasUpRecs: List<GasUpRec>? = null,
    // --------------码加注记录、技术通报实施记录\修理记录、重要记事
    // 码说明
    var codeDesc: String? = null,
    @Ignore
    // 码记录
    var codeUpRecs: List<CodeUpRec>? = null,
    @Ignore
    // 技术通报
    var tecReportImpRecs: List<TecReportImpRec>? = null,
    @Ignore
    // 修理记录
    var repairRecs: List<RepairRec>? = null,
    @Ignore
    // 重要记事
    var importantNotes: List<ImportantNote>? = null,

    )


