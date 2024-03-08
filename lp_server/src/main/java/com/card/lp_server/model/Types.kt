package com.card.lp_server.model

import androidx.annotation.Keep

@Keep
enum class Types(val key:Int,val value:String,) {
    BASE_INFO(1,"履历信息"),
    CODE_UP_REC(2,"__CodeUpRec"),
    EQU_MATCH(3,"__EquMatch"),
    EQU_REPLACE_REC(4,"__EquReplaceRec"),
    GASUP_REC(5,"__GasUpRec"),
    GJZB_REC(6,"__GJZBRec"),
    HANDOVER_REC(7,"__HandoverRec"),
    MT_REC(8,"__MtRec"),
    POWERON_REC(9,"__PoweronRec"),
    REPAIR_REC(10,"__RepairRec"),
    SORFTWARE_REC(11,"__SorftwareReplaceRec"),
    TECREPORTIMP_REC(12,"__TecReportImpRec"),
}
