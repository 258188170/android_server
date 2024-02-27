package com.card.lp_server.server

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.utils.responseJsonString
import fi.iki.elonen.NanoHTTPD
import java.net.URLDecoder
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class LServer(port: Int = 9988) : NanoHTTPD(port) {
//    init {
//        makeSecure(createSSLServerSocketFactory(), null)
//    }
    companion object {
        private const val TAG = "LServer"
    }


    override fun serve(session: IHTTPSession?): Response {
        Log.d(TAG, "线程 ${Thread.currentThread().name}");
        return if (session != null) dealWith(session) else responseJsonString(
            404,
            "",
            "Request not support!"
        )
    }

    private fun dealWith(session: IHTTPSession): Response {
        val headers = session.headers
        LogUtils.d("请求头：$headers")
        val uri = session.uri
        val method: Method? = session.method

        LogUtils.d("请求路径 uri：$uri-->>请求方式 method：$method")

        val paramsM = mutableMapOf<String, String>()
        session.parseBody(paramsM)
        LogUtils.d("session：${GsonUtils.toJson(paramsM)}")

        var postData = paramsM["postData"]
        if (postData?.isNotEmpty() == true) {
            postData = URLDecoder.decode(postData, "UTF-8")
        }
        val params = session.parameters
        val params2 = session.queryParameterString
        LogUtils.d("请求参数 paramsM：$paramsM")
        LogUtils.d("请求的json postData：$postData")
        LogUtils.d("请求参数 params：$params")
        LogUtils.d("请求参数 params2：$params2")
        return when (session.method) {
            Method.GET -> {
                when (session.uri) {
                    "api/home" ->
                        newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")

                    "api/about" ->
                        newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")

                    else -> responseJsonString(404, "", "Request not support!")
                }
            }

            Method.POST -> {
                when (session.uri) {
                    "/home" ->
                        newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是首页</body></html>")

                    "/about" ->
                        newFixedLengthResponse("<html><body style=\"font-size:40px;\">这里是关于</body></html>")

                    else -> responseJsonString(404, "", "Request not support!")
                }
            }

            else -> {
                responseJsonString(404, "", "Request not support!")
            }
        }

    }

    private fun createSSLServerSocketFactory(): SSLServerSocketFactory {
        val trustManagers = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate>? = null

                override fun checkClientTrusted(certs: Array<X509Certificate>?, authType: String?) {
                }

                override fun checkServerTrusted(certs: Array<X509Certificate>?, authType: String?) {
                }
            }
        )

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)
        return sslContext.serverSocketFactory
    }

}