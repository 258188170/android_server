package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.card.device.jsq.Jd014Jjsq1Device
import com.card.lp_server.card.device.jsq.Jd014Jjsq2Device
import com.card.lp_server.card.device.jsq.Jd014Jjsq3Device
import com.card.lp_server.model.PostParams
import com.card.lp_server.model.Types
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.model.TagEntity
import com.card.lp_server.room.entity.EquMatch
import com.card.lp_server.room.entity.EquReplaceRec
import com.card.lp_server.room.entity.GJZBRec
import com.card.lp_server.room.entity.GasUpRec
import com.card.lp_server.room.entity.HandoverRec
import com.card.lp_server.room.entity.ImportantNote
import com.card.lp_server.room.entity.MtRec
import com.card.lp_server.room.entity.PoweronRec
import com.card.lp_server.room.entity.RepairRec
import com.card.lp_server.room.entity.SorftwareReplaceRec
import com.card.lp_server.room.entity.TecReportImpRec
import com.card.lp_server.utils.JSQ1
import com.card.lp_server.utils.JSQ2
import com.card.lp_server.utils.JSQ3
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLl
import com.card.lp_server.utils.getPostParams
import com.card.lp_server.utils.getQueryParams
import com.card.lp_server.utils.responseJsonStringFail
import com.card.lp_server.utils.responseJsonStringSuccess
import fi.iki.elonen.NanoHTTPD
import java.nio.ByteBuffer


// 策略实现2: 处理POST请求
class PostRequestHandler : RequestHandlerStrategy {
    companion object {
        private const val TAG = "PostRequestHandler"
    }

    private val handlers = mapOf(
        //common
        UPDATE_DISPLAY to ::handleUpdateDisplay,
        COMMON_WRITE to ::handleCommonWrite,
        FIND_FILE_SIZE to ::handleFindFileSize,
        //dy
        ADD_BASE_INFO to ::handleBaseInfo,
        ADD_CODE_UP_REC to ::handleCodeUpRec,
        ADD_EQU_MATCH to ::handleAddEquMatch,
        ADD_EQU_REPLACE_REC to ::handleAddEquReplaceRec,
        ADD_GAS_UP_REC to ::handleAddGasUpRec,
        ADD_TEC_REPORT_IMP_REC to ::handleAddTecReportImpRec,
        ADD_SORFTWARE_REPLACE_REC to ::handleAddSorftwareReplaceRec,
        ADD_REPAIR_REC to ::handleAddRepairRec,
        ADD_POWERON_REC to ::handleAddPoweronRec,
        ADD_MT_REC to ::handleAddMtRec,
        ADD_IMPORTANT_NOTE to ::handleAddImportantNote,
        ADD_HANDOVER_REC to ::handleAddHandoverRec,
        ADD_GJZB_REC to ::handleAddGJZBRec,
        WRITE_DD_NUMBER to ::handleWriteDDNumber,
        WRITE_DYT_NUMBER to ::handleWriteDYDNumber,

        // Add more URL mappings as needed
    )

    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        val handler = handlers[uri]
        return handler?.invoke(session)
    }

    private fun handleHome(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")
    }

    private fun handleWriteDYDNumber(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams()
        if (postParams.isNullOrEmpty()) return responseJsonStringFail("Params is null or empty!")
        val par = GsonUtils.fromJson(postParams, PostParams::class.java)
        if (par.number.isNullOrEmpty()) return responseJsonStringFail("Params number is null or empty!")
        try {

            when (par.jsq) {
                JSQ1 -> {
                    val readFile = Jd014Jjsq1Device.getInstance().writeDyt(par.number)
                    return responseJsonStringSuccess(readFile)
                }

                JSQ2 -> {
                    val readFile = Jd014Jjsq2Device.getInstance().writeDyt(par.number)
                    return responseJsonStringSuccess(readFile)
                }

                JSQ3 -> {
                    val readFile = Jd014Jjsq3Device.getInstance().writeDyt(par.number)
                    return responseJsonStringSuccess(readFile)
                }

                else -> {
                    return responseJsonStringFail("Params jsq is null or empty")
                }
            }
        } catch (e: Exception) {
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleWriteDDNumber(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams()
        if (postParams.isNullOrEmpty()) return responseJsonStringFail("Params is null or empty!")
        val par = GsonUtils.fromJson(postParams, PostParams::class.java)
        if (par.number.isNullOrEmpty()) return responseJsonStringFail("Params number is null or empty!")
        try {

            when (par.jsq) {
                JSQ1 -> {
                    val readFile = Jd014Jjsq1Device.getInstance().writeDdbh(par.number)
                    return responseJsonStringSuccess(readFile)
                }

                JSQ2 -> {
                    val readFile = Jd014Jjsq2Device.getInstance().writeDdbh(par.number)
                    return responseJsonStringSuccess(readFile)
                }

                JSQ3 -> {
                    val readFile = Jd014Jjsq3Device.getInstance().writeDdbh(par.number)
                    return responseJsonStringSuccess(readFile)
                }

                else -> {
                    return responseJsonStringFail("Params jsq is null or empty")
                }
            }
        } catch (e: Exception) {
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddGasUpRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val gasUpRec = GsonUtils.fromJson(postParams, GasUpRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (gasUpRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddGasUpRec: $gasUpRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.GASUP_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(gasUpRec)).toByteArray()
                Log.d(TAG, "handleAddGasUpRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.GASUP_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleAddGasUpRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleAddGasUpRec: $string")
                val recordBean = GsonUtils.fromJson<List<GasUpRec>>(
                    String(readFile),
                    GsonUtils.getListType(GasUpRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == gasUpRec.dyNumber) {
                    recordBean.add(gasUpRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.GASUP_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleAddGasUpRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleAddGasUpRec: 写入 equMatch 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleAddGasUpRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddEquReplaceRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val equReplaceRec = GsonUtils.fromJson(postParams, EquReplaceRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (equReplaceRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddEquReplaceRec: $equReplaceRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.EQU_REPLACE_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(equReplaceRec)).toByteArray()
                Log.d(TAG, "handleAddEquReplaceRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.EQU_REPLACE_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleAddEquReplaceRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleAddEquReplaceRec: $string")
                val recordBean = GsonUtils.fromJson<List<EquReplaceRec>>(
                    String(readFile),
                    GsonUtils.getListType(EquReplaceRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == equReplaceRec.dyNumber) {
                    recordBean.add(equReplaceRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.EQU_REPLACE_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleAddEquReplaceRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleAddEquReplaceRec: 写入 equMatch 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleAddEquReplaceRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddEquMatch(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val equMatch = GsonUtils.fromJson(postParams, EquMatch::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (equMatch.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddEquMatch: $equMatch")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.EQU_MATCH.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(equMatch)).toByteArray()
                Log.d(TAG, "handleAddEquMatch: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.EQU_MATCH.value,
                        toByteArray
                    )
                Log.d(TAG, "handleAddEquMatch: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleAddEquMatch: $string")
                val recordBean = GsonUtils.fromJson<List<EquMatch>>(
                    String(readFile),
                    GsonUtils.getListType(EquMatch::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == equMatch.dyNumber) {
                    recordBean.add(equMatch)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.EQU_MATCH.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleAddEquMatch: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleAddEquMatch: 写入 equMatch 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleAddEquMatch: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }


    private fun handleCodeUpRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val codeUpRec = GsonUtils.fromJson(postParams, CodeUpRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (codeUpRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleCodeUpRec: $codeUpRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.CODE_UP_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(codeUpRec)).toByteArray()
                Log.d(TAG, "handleCodeUpRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.CODE_UP_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleCodeUpRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleCodeUpRec: $string")
                val recordBean = GsonUtils.fromJson<List<CodeUpRec>>(
                    String(readFile),
                    GsonUtils.getListType(CodeUpRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == codeUpRec.dyNumber) {
                    recordBean.add(codeUpRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.CODE_UP_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleCodeUpRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleCodeUpRec: 写入 CodeUpRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleCodeUpRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddTecReportImpRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val tecReportImpRec = GsonUtils.fromJson(postParams, TecReportImpRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (tecReportImpRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleTecReportImpRec: $tecReportImpRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.TECREPORTIMP_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(tecReportImpRec)).toByteArray()
                Log.d(TAG, "handleTecReportImpRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.TECREPORTIMP_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleTecReportImpRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleTecReportImpRec: $string")
                val recordBean = GsonUtils.fromJson<List<TecReportImpRec>>(
                    String(readFile),
                    GsonUtils.getListType(TecReportImpRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == tecReportImpRec.dyNumber) {
                    recordBean.add(tecReportImpRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.TECREPORTIMP_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleTecReportImpRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleTecReportImpRec: 写入 TecReportImpRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleTecReportImpRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddSorftwareReplaceRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val sorftwareReplaceRec = GsonUtils.fromJson(postParams, SorftwareReplaceRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (sorftwareReplaceRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleSorftwareReplaceRec: $sorftwareReplaceRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.SORFTWARE_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(sorftwareReplaceRec)).toByteArray()
                Log.d(TAG, "handleSorftwareReplaceRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.SORFTWARE_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleSorftwareReplaceRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleSorftwareReplaceRec: $string")
                val recordBean = GsonUtils.fromJson<List<SorftwareReplaceRec>>(
                    String(readFile),
                    GsonUtils.getListType(SorftwareReplaceRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == sorftwareReplaceRec.dyNumber) {
                    recordBean.add(sorftwareReplaceRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.SORFTWARE_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleSorftwareReplaceRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleSorftwareReplaceRec: 写入 SorftwareReplaceRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleSorftwareReplaceRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddRepairRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val repairRec = GsonUtils.fromJson(postParams, RepairRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (repairRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleRepairRec: $repairRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.REPAIR_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(repairRec)).toByteArray()
                Log.d(TAG, "handleRepairRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.REPAIR_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleRepairRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleRepairRec: $string")
                val recordBean = GsonUtils.fromJson<List<RepairRec>>(
                    String(readFile),
                    GsonUtils.getListType(RepairRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == repairRec.dyNumber) {
                    recordBean.add(repairRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.REPAIR_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleRepairRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleRepairRec: 写入 RepairRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleRepairRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddPoweronRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val poweronRec = GsonUtils.fromJson(postParams, PoweronRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (poweronRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handlePoweronRec: $poweronRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.POWERON_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(poweronRec)).toByteArray()
                Log.d(TAG, "handlePoweronRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.POWERON_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handlePoweronRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handlePoweronRec: $string")
                val recordBean = GsonUtils.fromJson<List<PoweronRec>>(
                    String(readFile),
                    GsonUtils.getListType(PoweronRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == poweronRec.dyNumber) {
                    recordBean.add(poweronRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.POWERON_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handlePoweronRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handlePoweronRec: 写入 PoweronRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handlePoweronRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddMtRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val mtRec = GsonUtils.fromJson(postParams, MtRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (mtRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleMtRec: $mtRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.MT_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(mtRec)).toByteArray()
                Log.d(TAG, "handleMtRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.MT_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleMtRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleMtRec: $string")
                val recordBean = GsonUtils.fromJson<List<MtRec>>(
                    String(readFile),
                    GsonUtils.getListType(MtRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == mtRec.dyNumber) {
                    recordBean.add(mtRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.MT_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleMtRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleMtRec: 写入 MtRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleMtRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddImportantNote(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val importantNote = GsonUtils.fromJson(postParams, ImportantNote::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (importantNote.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleImportantNote: $importantNote")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.TECREPORTIMP_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(importantNote)).toByteArray()
                Log.d(TAG, "handleImportantNote: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.TECREPORTIMP_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleImportantNote: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleImportantNote: $string")
                val recordBean = GsonUtils.fromJson<List<ImportantNote>>(
                    String(readFile),
                    GsonUtils.getListType(ImportantNote::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == importantNote.dyNumber) {
                    recordBean.add(importantNote)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.TECREPORTIMP_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleImportantNote: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleImportantNote: 写入 ImportantNote 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleImportantNote: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddHandoverRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val handoverRec = GsonUtils.fromJson(postParams, HandoverRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (handoverRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleHandoverRec: $handoverRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.HANDOVER_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(handoverRec)).toByteArray()
                Log.d(TAG, "handleHandoverRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.HANDOVER_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleHandoverRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleHandoverRec: $string")
                val recordBean = GsonUtils.fromJson<List<HandoverRec>>(
                    String(readFile),
                    GsonUtils.getListType(HandoverRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == handoverRec.dyNumber) {
                    recordBean.add(handoverRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.HANDOVER_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleHandoverRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleHandoverRec: 写入 HandoverRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleHandoverRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleAddGJZBRec(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val gjzbRec = GsonUtils.fromJson(postParams, GJZBRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (gjzbRec.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleGJZBRec: $gjzbRec")
        try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.GJZB_REC.value)
            val writeFile: Boolean
            if (readFile == null) {
                val toByteArray = GsonUtils.toJson(arrayListOf(gjzbRec)).toByteArray()
                Log.d(TAG, "handleGJZBRec: toByteArray $toByteArray")
                writeFile = LonbestCard.getInstance()
                    .writeFile(
                        Types.GJZB_REC.value,
                        toByteArray
                    )
                Log.d(TAG, "handleGJZBRec: writeFile $writeFile")
                return if (writeFile) {
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val string = String(readFile)
                Log.d(TAG, "handleGJZBRec: $string")
                val recordBean = GsonUtils.fromJson<List<GJZBRec>>(
                    String(readFile),
                    GsonUtils.getListType(GJZBRec::class.java)
                ).toMutableList()
                return if (recordBean.first().dyNumber == gjzbRec.dyNumber) {
                    recordBean.add(gjzbRec)
                    writeFile = LonbestCard.getInstance()
                        .writeFile(
                            Types.GJZB_REC.value,
                            GsonUtils.toJson(recordBean).toByteArray()
                        )
                    Log.d(TAG, "handleGJZBRec: writeFile $writeFile")
                    if (writeFile) {
                        responseJsonStringSuccess(true)
                    } else {
                        Log.d(TAG, "handleGJZBRec: 写入 GJZBRec 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleGJZBRec: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }
    }

    private fun handleBaseInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val fromJson = GsonUtils.fromJson(postParams, RecordBean::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (fromJson.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddBaseInfo: $fromJson")

        try {
            //读数据
            val readFile = LonbestCard.getInstance()
                .readFile(Types.BASE_INFO.value)
            val writeFile: Boolean
            if (readFile == null) {
                writeFile = LonbestCard.getInstance()
                    .writeFile(Types.BASE_INFO.value, postParams.toByteArray())
                Log.d(TAG, "handleBaseInfo: writeFile $writeFile")
                return if (writeFile) {
                    response(fromJson)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val recordBean = GsonUtils.fromJson(String(readFile), RecordBean::class.java)
                return if (recordBean.dyNumber == fromJson.dyNumber) {
                    writeFile = LonbestCard.getInstance()
                        .writeFile(Types.BASE_INFO.value, postParams.toByteArray())
                    Log.d(TAG, "handleBaseInfo: writeFile $writeFile")
                    if (writeFile) {
                        response(recordBean)
                    } else {
                        Log.d(TAG, "handleBaseInfo: 写入 RecordBean 失败")
                        responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleBaseInfo: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
        }

    }

    private fun response(fromJson: RecordBean): NanoHTTPD.Response {
        return if (fromJson.isEink == true) {
            val generateBitMapForLl = generateBitMapForLl(fromJson)
            val convertBitmapToBinary = convertBitmapToBinary(generateBitMapForLl)
            val updateEInk =
                LonbestCard.getInstance().updateEInk(convertBitmapToBinary)
            if (updateEInk) {
                responseJsonStringSuccess(true)
            } else {
                Log.d(TAG, "handleBaseInfo: 更新屏幕失败")
                responseJsonStringSuccess(false, "更新屏幕失败,请重试!")
            }
        } else {
            Log.d(TAG, "handleBaseInfo: 不需要刷屏")
            responseJsonStringSuccess(true)
        }
    }

    private fun handleFindFileSize(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val fromJson = GsonUtils.fromJson(postParams, TagEntity::class.java)
            ?: return responseJsonStringFail("屏幕数据不能为空!")
        Log.d(TAG, "handleUpdateDisplay: $fromJson")
        if (fromJson.fileName == null) return responseJsonStringFail("参数不能为空")
        return try {
            val findFileSize = LonbestCard.getInstance().findFileSize(fromJson.fileName)

            responseJsonStringSuccess(ByteBuffer.wrap(findFileSize).int)
        } catch (e: Exception) {
            e.printStackTrace()
            responseJsonStringFail(e.message)
        }


    }

    private fun handleCommonWrite(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val fromJson = GsonUtils.fromJson(postParams, TagEntity::class.java)
            ?: return responseJsonStringFail("屏幕数据不能为空!")
        Log.d(TAG, "handleUpdateDisplay: $fromJson")
        if (fromJson.fileName == null || fromJson.data == null) return responseJsonStringFail("参数不能为空")

        return try {
            val writeFile = LonbestCard.getInstance().writeFile(fromJson.fileName, fromJson.data)
            if (writeFile)
                responseJsonStringSuccess(true)
            else
                responseJsonStringSuccess(false, "操作失败")
        } catch (e: Exception) {
            e.printStackTrace()
            responseJsonStringFail(e.message)
        }

    }

    private fun handleUpdateDisplay(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val fromJson = GsonUtils.fromJson(postParams, TagEntity::class.java).data
            ?: return responseJsonStringFail("屏幕数据不能为空!")
        Log.d(TAG, "handleUpdateDisplay: $fromJson")
        return try {
            val updateEInk = LonbestCard.getInstance().updateEInk(fromJson)
            if (updateEInk)
                responseJsonStringSuccess(true)
            else
                responseJsonStringSuccess(false, "更新屏幕失败")
        } catch (e: Exception) {
            e.printStackTrace()
            responseJsonStringFail(e.message)
        }
    }
    // Add more handler functions for other URLs if needed
}

