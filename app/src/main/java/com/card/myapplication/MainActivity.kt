package com.card.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.appContainer
import com.card.lp_server.appContext
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.server.LServer
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rxhttp.toFlow
import rxhttp.wrapper.param.RxHttp

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appContext
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

    fun testroom(view: View) {

        lifecycleScope.launch(Dispatchers.IO) {
            appContainer.recordRepository.insertItem(RecordBean())
        }
    }

}