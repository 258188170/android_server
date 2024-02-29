package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.model.Types
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.model.TagEntity
import com.card.lp_server.room.entity.EquMatch
import com.card.lp_server.room.entity.EquReplaceRec
import com.card.lp_server.room.entity.GasUpRec
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLl
import com.card.lp_server.utils.getPostParams
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
        ENCODE_AND_UPDATE_EINK to ::handleEncode,
        "/api/addEquMatch" to ::handleAddEquMatch,
        "/api/addEquReplaceRec" to ::handleAddEquReplaceRec,
        "/api/addGasUpRec" to ::handleAddGasUpRec,

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
                    GsonUtils.getListType(CodeUpRec::class.java)
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
                    GsonUtils.getListType(CodeUpRec::class.java)
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
                    GsonUtils.getListType(CodeUpRec::class.java)
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

    private fun handleEncode(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val fromJson = GsonUtils.fromJson(postParams, RecordBean::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (fromJson.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddBaseInfo: $fromJson")
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")
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
                    responseJsonStringSuccess(true)
                } else {
                    responseJsonStringSuccess(false, "操作失败!")
                }
            } else {
                val recordBean = GsonUtils.fromJson(String(readFile), RecordBean::class.java)
                if (recordBean.dyNumber == fromJson.dyNumber) {
                    writeFile = LonbestCard.getInstance()
                        .writeFile(Types.BASE_INFO.value, postParams.toByteArray())
                    Log.d(TAG, "handleBaseInfo: writeFile $writeFile")
                    if (writeFile) {
                        return if (recordBean.isEink == true) {
                            val generateBitMapForLl = generateBitMapForLl(recordBean)
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
                    } else {
                        Log.d(TAG, "handleBaseInfo: 写入 RecordBean 失败")
                        return responseJsonStringSuccess(false, "操作失败!")
                    }
                } else {
                    Log.d(TAG, "handleBaseInfo: 要写入标签弹号与标签内不一致")
                    return responseJsonStringSuccess(false, "要写入标签弹号与当前标签内不一致!")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return responseJsonStringFail(e.message)
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

