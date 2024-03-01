package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.card.device.jsq.Jd014Jjsq1Device
import com.card.lp_server.card.device.jsq.Jd014Jjsq2Device
import com.card.lp_server.card.device.jsq.Jd014Jjsq3Device
import com.card.lp_server.model.Types
import com.card.lp_server.utils.FILE_NAME
import com.card.lp_server.utils.TAG
import com.card.lp_server.utils.TYPE_NUMBER
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLlFormat
import com.card.lp_server.utils.getQueryParams
import com.card.lp_server.utils.getType
import com.card.lp_server.utils.responseJsonStringFail
import com.card.lp_server.utils.responseJsonStringSuccess
import com.card.lp_server.utils.stringConvertToList
import fi.iki.elonen.NanoHTTPD

class GetRequestHandler : RequestHandlerStrategy {
    private val handlers = mapOf(
        LIST_FILES to ::handleListFile,
        READ_FILE to ::handleReadFile,
        DELETE_FILE to ::handleDeleteFile,
        CLEAR_TAG to ::handleClearTag,
        TAG_INFO to ::handleTagInfo,
        TAG_VERSION to ::handleTagVersion,
        GET_BASE_INFO to ::handleBaseInfo,
        GET_TYPE_LIST to ::handleTypeList,
        GET_TYPE_LIST to ::handleTypeList,
        //014设备接口
        "api/jsq1_read" to ::handleJSQ1Read,
        "api/jsq1_list" to ::handleJSQ1List,
        "api/jsq2_read" to ::handleJSQ2Read,
        "api/jsq3_read" to ::handleJSQ3Read,


        )

    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        val handler = handlers[uri]
        return handler?.invoke(session)
    }

    private fun handleJSQ2Read(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val readFile = Jd014Jjsq2Device.getInstance().readFile()
            responseJsonStringSuccess(readFile)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleJSQ3Read(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val readFile = Jd014Jjsq3Device.getInstance().readFile()
            responseJsonStringSuccess(readFile)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleJSQ1List(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val readFile = Jd014Jjsq1Device.getInstance().jwdList
            responseJsonStringSuccess(readFile)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleJSQ1Read(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val readFile = Jd014Jjsq1Device.getInstance().readFile()
            responseJsonStringSuccess(readFile)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleTypeList(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[TYPE_NUMBER]?.first()
        val type = queryParams.getType()
        if (type.isEmpty()) return responseJsonStringFail("请传入要读取类型")
        return try {
            val readFile = LonbestCard.getInstance().readFile(type)
            responseJsonStringSuccess(String(readFile))
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleBaseInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val readFile = LonbestCard.getInstance().readFile(Types.BASE_INFO.value)
            responseJsonStringSuccess(String(readFile))
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }

    }

    private fun handleListFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val listFiles = LonbestCard.getInstance().listFiles()
            responseJsonStringSuccess(stringConvertToList(listFiles))
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleTagVersion(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val version = LonbestCard.getInstance().version
            responseJsonStringSuccess(version)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }

    }

    private fun handleTagInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return try {
            val info = LonbestCard.getInstance().info
            responseJsonStringSuccess(ConvertUtils.bytes2String(info))
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleReadFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[FILE_NAME]?.first()
            ?: return responseJsonStringFail("参数[FILE_NAME]不能为空")
        return try {
            val readFile = LonbestCard.getInstance().readFile(queryParams)
            responseJsonStringSuccess(readFile)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }

    private fun handleDeleteFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[FILE_NAME]?.first()
            ?: return responseJsonStringFail("参数[FILE_NAME]不能为空")
        return try {
            val deleteFile = LonbestCard.getInstance().deleteFile(queryParams)
            if (deleteFile) {
                responseJsonStringSuccess(true)
            } else {
                responseJsonStringSuccess(false, "操作失败")
            }
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }

    }

    private fun handleClearTag(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {


        return try {
            val listFiles = LonbestCard.getInstance().listFiles()
            val stringConvertToList = stringConvertToList(listFiles)

            stringConvertToList.forEach {
                val tag = LonbestCard.getInstance().deleteFile(it)
                if (!tag) {
                    return responseJsonStringFail()
                }
            }
            val generateBitMapForLlFormat = generateBitMapForLlFormat()
            val convertBitmapToBinary = convertBitmapToBinary(generateBitMapForLlFormat)
            Log.d(TAG, "handleClearTag: ${convertBitmapToBinary.size}")
            val updateEInk = LonbestCard.getInstance().updateEInk(convertBitmapToBinary)
            if (updateEInk)
                responseJsonStringSuccess(true)
            else
                responseJsonStringSuccess(false)
        } catch (e: Exception) {
            responseJsonStringFail(e.message)
        }
    }
}
