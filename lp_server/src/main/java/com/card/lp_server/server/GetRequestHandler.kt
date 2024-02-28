import com.blankj.utilcode.util.ConvertUtils
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.server.RequestHandlerStrategy
import com.card.lp_server.utils.logD
import com.card.lp_server.utils.responseJsonStringFail
import com.card.lp_server.utils.responseJsonStringSuccess
import fi.iki.elonen.NanoHTTPD

class GetRequestHandler : RequestHandlerStrategy {
    private val TAG = "GetRequestHandler"
    private val handlers = mapOf(
        "/api/list_file" to ::handleCommonListFile,
        "/api/about" to ::handleAbout
        // Add more URL mappings as needed
    )

    override fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        val uri = session.uri
        val handler = handlers[uri]
        return handler?.invoke(session)
    }

    private fun handleCommonListFile(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        try {
            val findAndOpenHIDDevice = HIDCommunicationUtil.instance.findAndOpenHIDDevice()
            if (findAndOpenHIDDevice) {
                val listFiles = LonbestCard.getInstance().listFiles()
                if (listFiles.isNotEmpty()) {
                    val bytes2String = ConvertUtils.bytes2String(
                        listFiles
                    )
                    logD(bytes2String)
                    return bytes2String.responseJsonStringSuccess()
                }
            }
        } catch (e: Exception) {
            return responseJsonStringFail(e.message)
        }
        return arrayListOf<String>().responseJsonStringFail("设备未连接,请重试", -100)
    }

    private fun handleAbout(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")
    }
}
