package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.model.Types
import com.card.lp_server.utils.FILE_NAME
import com.card.lp_server.utils.TAG
import com.card.lp_server.utils.TYPE_NUMBER
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLlFormat
import com.card.lp_server.utils.getQueryParams
import com.card.lp_server.utils.getType
import com.card.lp_server.utils.handleResponse
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
    )

    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        val handler = handlers[uri]
        return handler?.invoke(session)
    }

    private fun handleTypeList(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[TYPE_NUMBER]?.first()
        val type = queryParams.getType()
        if (type.isEmpty()) return responseJsonStringFail("请传入要读取类型")
        return handleResponse {
            val readFile = LonbestCard.getInstance().readFile(type)
            String(readFile)
        }
    }

    private fun handleBaseInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handleResponse {
            val readFile = LonbestCard.getInstance().readFile(Types.BASE_INFO.value)
            String(readFile)
        }
    }

    private fun handleListFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handleResponse {
            val listFiles = LonbestCard.getInstance().listFiles()
            stringConvertToList(listFiles)
        }
    }

    private fun handleTagVersion(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handleResponse {
            val version = LonbestCard.getInstance().version
            version
        }
    }

    private fun handleTagInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handleResponse {
            val info = LonbestCard.getInstance().info
            ConvertUtils.bytes2String(info)
        }
    }

    private fun handleReadFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[FILE_NAME]?.first()
            ?: return responseJsonStringFail("参数[FILE_NAME]不能为空")
        return handleResponse {
            val readFile = LonbestCard.getInstance().readFile(queryParams)
            readFile
        }
    }

    private fun handleDeleteFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[FILE_NAME]?.first()
            ?: return responseJsonStringFail("参数[FILE_NAME]不能为空")
        return handleResponse {
            val deleteFile = LonbestCard.getInstance().deleteFile(queryParams)
            deleteFile
        }
    }

    private fun handleClearTag(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handleResponse {
            val listFiles = LonbestCard.getInstance().listFiles()
            val stringConvertToList = stringConvertToList(listFiles)

            stringConvertToList.forEach {
                val tag = LonbestCard.getInstance().deleteFile(it)
                if (!tag) {
                    return@handleResponse responseJsonStringFail(false, msg = "删除文件失败,请重试")
                }
            }
            val generateBitMapForLlFormat = generateBitMapForLlFormat()
            val convertBitmapToBinary = convertBitmapToBinary(generateBitMapForLlFormat)
            Log.d(TAG, "handleClearTag: ${convertBitmapToBinary.size}")
            val updateEInk = LonbestCard.getInstance().updateEInk(convertBitmapToBinary)
            updateEInk
        }
    }
}
