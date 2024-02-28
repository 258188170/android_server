package com.card.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.mAppContainer
import com.card.lp_server.room.entity.TagEntity
import com.card.lp_server.room.entity.RecordBean
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
        val ipAddress = NetworkUtils.getIPAddress(true)
        Log.d(TAG, "onCreate: $ipAddress")
    }

    fun testHttp(view: View) {

        lifecycleScope.launchWhenCreated {
            RxHttp.get("/api/clear_tag")  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }

    }

    fun testroom(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            mAppContainer.mRecordRepository.insertItem(RecordBean())

        }
    }

    fun testcon(view: View) {
        HIDCommunicationUtil.instance.findAndOpenHIDDevice()
    }

    fun testHttpPost(view: View) {
        lifecycleScope.launchWhenCreated {
            RxHttp.postBody("/api/update_display")  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .setBody(TagEntity(byteArrayOf(1,2)))
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }
    }

}