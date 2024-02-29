package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.mAppContainer
import com.card.lp_server.model.Types
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.model.TagEntity
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
        val fromJson = GsonUtils.fromJson(postParams, CodeUpRec::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (fromJson.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddBaseInfo: $fromJson")


        return try {
            val writeFile =
                LonbestCard.getInstance()
                    .writeFile(Types.CODE_UP_REC.value, postParams.toByteArray())
            if (writeFile) {
                mAppContainer.mCodeUpRecRepository.insertItem(fromJson)
                responseJsonStringSuccess(true)
            } else {
                responseJsonStringSuccess(false, "操作失败")
            }
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }

    }

    private fun handleBaseInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParams = session.getPostParams() ?: return responseJsonStringFail("参数不能为空!")
        val fromJson = GsonUtils.fromJson(postParams, RecordBean::class.java)
            ?: return responseJsonStringFail("参数不能为空!")
        if (fromJson.dyNumber.isNullOrEmpty())
            return responseJsonStringFail("dyNumber is not empty")
        Log.d(TAG, "handleAddBaseInfo: $fromJson")

        return try {
            val readFile = LonbestCard.getInstance()
                .readFile(Types.BASE_INFO.value)
            var writeFile = false
            if (readFile == null) {
                writeFile = LonbestCard.getInstance()
                    .writeFile(Types.BASE_INFO.value, postParams.toByteArray())
                mAppContainer.mRecordRepository.insertItem(fromJson)
                Log.d(TAG, "handleBaseInfo: writeFile $writeFile")
                responseJsonStringSuccess(writeFile)
            } else {
                val recordBean = GsonUtils.fromJson(String(readFile), RecordBean::class.java)
                if (recordBean.dyNumber == fromJson.dyNumber) {
                    writeFile = LonbestCard.getInstance()
                        .writeFile(Types.BASE_INFO.value, postParams.toByteArray())
                    mAppContainer.mRecordRepository.updateItem(fromJson)
                    Log.d(TAG, "handleBaseInfo: writeFile $writeFile")
                    if (writeFile) {
                        if (recordBean.isEink == true) {
                            val generateBitMapForLl = generateBitMapForLl(recordBean)
                            val convertBitmapToBinary = convertBitmapToBinary(generateBitMapForLl)
                            val updateEInk =
                                LonbestCard.getInstance().updateEInk(convertBitmapToBinary)
                            if (updateEInk) {
                                responseJsonStringSuccess(true)
                            } else {
                                responseJsonStringSuccess(false, "更新屏幕失败,请重试")
                            }
                        }
                    }
                    responseJsonStringSuccess(false)
                } else {
                    Log.d(TAG, "handleBaseInfo: 要写入标签弹号与标签内不一致")
                    responseJsonStringSuccess(false, "要写入标签弹号与标签内不一致")
                }
            }
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
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
            responseJsonStringFail(e.message)
        }
    }
    // Add more handler functions for other URLs if needed
}

