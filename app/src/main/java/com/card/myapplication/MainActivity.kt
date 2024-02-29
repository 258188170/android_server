package com.card.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.mAppContainer
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.room.entity.TagEntity
import com.card.lp_server.server.CLEAR_TAG
import com.card.lp_server.server.LIST_FILES
import com.card.lp_server.server.READ_FILE
import com.card.lp_server.server.UPDATE_DISPLAY
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rxhttp.toFlow
import rxhttp.wrapper.param.RxHttp


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    lateinit var tvFileList: AppCompatTextView
    lateinit var scroll: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvFileList = findViewById(R.id.tvFileList)
        scroll = findViewById(R.id.scroll)

        val ipAddress = NetworkUtils.getIPAddress(true)
        Log.d(TAG, "onCreate: $ipAddress")
    }
    fun writeFile(view: View) {
//        requestPost()
    }

    fun testHttp(view: View) {
        requestGet(CLEAR_TAG)
    }
    fun listFiles(view: View) {
        requestGet(LIST_FILES)
    }
    fun readFile(view: View) {
        requestGet(READ_FILE)
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
        requestPost(UPDATE_DISPLAY, TagEntity(data = byteArrayOf()))
    }

    fun add_base_info(view: View) {
        lifecycleScope.launchWhenCreated {
            RxHttp.postBody("/api/addBaseInfo")  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .setBody(RecordBean(dyNumber = "123"))
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }
    }

    fun testUpdateDisplay(view: View) {
        lifecycleScope.launchWhenCreated {
            val generateBitMapForLl = generateBitMapForLl()
            val convertBitmapToBinary =
                convertBitmapToBinary(generateBitMapForLl)

            requestPost(UPDATE_DISPLAY, TagEntity(data = convertBitmapToBinary))

        }

    }

    fun get_base_info(view: View) {
        lifecycleScope.launchWhenCreated {
            RxHttp.get("/api/getBaseInfo")  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }
    }

    fun requestGet(url: String) {
        tvFileList.text = "${tvFileList.text}\n\n request: GET path = $url"
        lifecycleScope.launchWhenCreated {
            RxHttp.get(url)  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                    tvFileList.text = "${tvFileList.text}\n response: path = $url \n result: $it"
                    tvFileList.viewTreeObserver
                        .addOnPreDrawListener { // 在 View 绘制完成后的回调操作
                            scroll.fullScroll(ScrollView.FOCUS_DOWN)
                            true
                        }

                }

        }
    }
    fun requestPost(url: String, body: Any) {
        tvFileList.text = "${tvFileList.text}\n\n request: POST path = $url"

        lifecycleScope.launchWhenCreated {
            RxHttp.postBody(url)  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .setBody(body)
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {
                    tvFileList.text = "${tvFileList.text}\n response: path = $url \n result: $it"
                    //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }
    }

}