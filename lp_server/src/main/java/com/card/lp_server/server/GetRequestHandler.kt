import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.server.RequestHandlerStrategy
import com.card.lp_server.utils.FILE_NAME
import com.card.lp_server.utils.getQueryParams
import com.card.lp_server.utils.handleResponse
import com.card.lp_server.utils.logD
import com.card.lp_server.utils.responseJsonStringFail
import com.card.lp_server.utils.responseJsonStringSuccess
import com.card.lp_server.utils.stringConvertToList
import fi.iki.elonen.NanoHTTPD

class GetRequestHandler : RequestHandlerStrategy {
    private val TAG = "GetRequestHandler"
    private val handlers = mapOf(
        "/api/list_file" to ::handleListFile,
        "/api/read_file" to ::handleReadFile,
        "/api/about" to ::handleAbout
        // Add more URL mappings as needed
    )

    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        val handler = handlers[uri]
        return handler?.invoke(session)
    }

    private fun handleListFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {

        return handleResponse {
            val listFiles = LonbestCard.getInstance().listFiles()
            stringConvertToList(listFiles)
        }
    }

    private fun handleReadFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val queryParams = session.getQueryParams()[FILE_NAME]?.first()
            ?: return responseJsonStringFail("参数[FILE_NAME]不能为空")
        return handleResponse {
            val data = LonbestCard.getInstance().readFile(queryParams)
            ConvertUtils.bytes2HexString(data)
        }
    }

    private fun handleAbout(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")
    }
}
