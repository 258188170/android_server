package com.card.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.ServerSingleton
import com.card.lp_server.server.LServer
import fi.iki.elonen.NanoHTTPD
import rxhttp.toFlow
import rxhttp.wrapper.param.RxHttp

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val lServer by lazy {
        LServer(9988)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ServerSingleton.init(this)
    }

    fun testHttp(view: View) {
        Log.d(TAG, "testHttp: ")
        lifecycleScope.launchWhenCreated {
            RxHttp.get("/home")  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        lServer.closeAllConnections()
    }

}