import android.util.Log
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
                val stringConvertToList = stringConvertToList(listFiles)
                logD(stringConvertToList)
                return stringConvertToList.responseJsonStringSuccess()
            }
            return arrayListOf<String>().responseJsonStringFail("设备未连接,请重试")
        } catch (e: Exception) {
            return responseJsonStringFail(e.message)
        }
    }

    private fun stringConvertToList(listFiles: ByteArray?): List<String> {
        val bytes2HexString = ConvertUtils.bytes2HexString(listFiles)
        val substringBeforeLast = bytes2HexString.substringBeforeLast("0A00")
        val replace = substringBeforeLast.replace("0A00", "0A")
        Log.d(TAG, "listFiles: $replace")
        val hexString2Bytes = ConvertUtils.hexString2Bytes(replace)
        val string = String(hexString2Bytes)
        Log.d(TAG, "listFiles: $string")
        val split = string.split("\n")
        Log.d(TAG, "listFiles: ${split.size}")
        return split
    }

    private fun handleAbout(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")
    }
}
