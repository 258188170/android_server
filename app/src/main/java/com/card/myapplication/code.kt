package com.card.myapplication

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.blankj.utilcode.BuildConfig
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.TimeoutCancellationException
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.exception.HttpStatusCodeException
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.ssl.HttpsUtils
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession


val LifecycleOwner.lifecycleScope: LifecycleCoroutineScope
    get() = lifecycle.coroutineScope


//建议在Application中调用
fun init() {
    val sslParams = HttpsUtils.getSslSocketFactory()
    val client: OkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
        .hostnameVerifier(HostnameVerifier { _: String?, _: SSLSession? -> true }) //忽略host验证
        .build()
    RxHttpPlugins.init(client) //自定义OkHttpClient对象
        .setDebug(BuildConfig.DEBUG, false, 2) //调试模式/分段打印/json数据格式化输出
        .setOnParamAssembly { }
}


val Throwable.code: Int
    get() =
        when (this) {
            is HttpStatusCodeException -> this.statusCode //Http状态码异常
            is ParseException -> this.errorCode.toIntOrNull() ?: -1     //业务code异常
            else -> -1
        }

val Throwable.msg: String
    get() {
        //okhttp全局设置超时
        //rxjava中的timeout方法超时
        //协程超时
        return when (this) {
            is UnknownHostException -> { //网络异常
                "当前无网络，请检查你的网络设置"
            }

            is SocketTimeoutException, is TimeoutException, is TimeoutCancellationException -> {
                "连接超时,请稍后再试"
            }

            is ConnectException -> {
                "网络不给力，请稍候重试！"
            }

            is HttpStatusCodeException -> {               //请求失败异常
                "Http状态码异常"
            }

            is JsonSyntaxException -> {  //请求成功，但Json语法异常,导致解析失败
                "数据解析失败,请检查数据是否正确"
            }

            is ParseException -> {       // ParseException异常表明请求成功，但是数据不正确
                this.message ?: errorCode   //msg为空，显示code
            }

            else -> {
                "请求失败，请稍后再试"
            }
        }
    }
