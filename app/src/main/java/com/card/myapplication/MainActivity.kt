package com.card.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.mAppContainer
import com.card.lp_server.model.TagEntity
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.server.ADD_BASE_INFO
import com.card.lp_server.server.ADD_CODE_UP_REC
import com.card.lp_server.server.CLEAR_TAG
import com.card.lp_server.server.COMMON_WRITE
import com.card.lp_server.server.DELETE_FILE
import com.card.lp_server.server.ENCODE_AND_UPDATE_EINK
import com.card.lp_server.server.FIND_FILE_SIZE
import com.card.lp_server.server.GET_BASE_INFO
import com.card.lp_server.server.GET_TYPE_LIST
import com.card.lp_server.server.LIST_FILES
import com.card.lp_server.server.READ_FILE
import com.card.lp_server.server.TAG_INFO
import com.card.lp_server.server.TAG_VERSION
import com.card.lp_server.server.UPDATE_DISPLAY
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rxhttp.toFlow
import rxhttp.wrapper.param.RxHttp
import java.io.InputStream
import java.io.ByteArrayOutputStream
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private
    companion object {
        private const val TAG = "MainActivity"
        private const val PICK_FILE_REQUEST_CODE = 1
        private const val REQUEST_READ_EXTERNAL_STORAGE = 2
        private const val TEST_NAME = "test_name"
    }

    private val progressDialog by lazy {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("加载中...")
        progressDialog.setCancelable(true) // 设置是否可以通过返回键取消
        progressDialog
    }

    private val stringBuilder by lazy {
        StringBuilder() // 创建一个空的 StringBuilder 对象
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 用户授予了权限，可以执行相应操作，比如读取文件
                // 在这里执行文件读取操作
                pickFile()
            } else {
                // 用户拒绝了权限请求
                // 在这里处理拒绝权限的情况
                ToastUtils.showLong("拒绝了权限")
            }
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
        // 检查是否有读取外部存储权限，如果没有则请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 对于 Android 11 及以上版本，需要请求 MANAGE_EXTERNAL_STORAGE 权限
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            } else {
                // 已经获得了权限，可以执行相应操作，比如读取文件
                // 在这里执行文件读取操作
                pickFile()

            }
        } else {
            // 对于 Android 10 及以下
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                // 如果没有权限，请求权限
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE
                )
            } else {
                // 如果已经有权限，执行文件读取操作
                pickFile()
            }

        }

    }

    // 在需要选择文件的地方调用该方法
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*") // 设置文件类型，这里表示任意类型的文件
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    fun testHttp(view: View) {
        requestGet(CLEAR_TAG)
    }

    fun listFiles(view: View) {
        requestGet(LIST_FILES)
    }

    fun deleteFile(view: View) {
        requestGet("$DELETE_FILE?fileName=$TEST_NAME")
    }

    fun tagInfo(view: View) {
        requestGet(TAG_INFO)
    }

    fun readFile(view: View) {
        requestGet("$READ_FILE?fileName=$TEST_NAME")
    }

    fun testroom(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            mAppContainer.mRecordRepository.insertItem(RecordBean())
        }
    }

    fun testcon(view: View) {
        HIDCommunicationUtil.instance.findAndOpenHIDDevice()
    }

    fun add_base_info(view: View) {
        val randomNumber = Random.nextInt(2, 130) // 生成一个范围在 2 到 12 之间的随机数

        requestPost(ADD_BASE_INFO, RecordBean(dyNumber = "$randomNumber", isEink = true))
    }

    fun testUpdateDisplay(view: View) {
        val generateBitMapForLl = generateBitMapForLl(RecordBean(dyNumber = "123"))
        val convertBitmapToBinary = convertBitmapToBinary(generateBitMapForLl)
        runOnUiThread {
            requestPost(UPDATE_DISPLAY, TagEntity(data = convertBitmapToBinary))

        }


    }


    private fun requestGet(url: String) {
        show("\n\n request: GET path = $url")
        lifecycleScope.launch(Dispatchers.IO) {
            RxHttp.get(url)  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                    dismiss("\n response: path = $url \n result: $it")
                }

        }
    }

    private fun requestPost(url: String, body: Any) {
        show("\n\n request: POST path = $url \n param body:${GsonUtils.toJson(body)}")

        lifecycleScope.launch(Dispatchers.IO) {
            RxHttp.postBody(url)  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .setBody(body)
                .toFlow<String>()       //第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {
                    dismiss("\n response: path = $url \n result: $it")

                    //第三步，调用collect方法发起请求
                    LogUtils.d(it)
                }

        }
    }

    private fun show(str: String) {
        progressDialog.show()
        stringBuilder.append(str)
        tvFileList.text = stringBuilder
    }

    private suspend fun dismiss(str: String) {
        progressDialog.dismiss()
        stringBuilder.append(str)
        withContext(Dispatchers.Main) {
            progressDialog.dismiss()

            tvFileList.text = stringBuilder

            tvFileList.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    tvFileList.viewTreeObserver.removeOnPreDrawListener(this) // 确保只触发一次

                    tvFileList.post {
                        scroll.fullScroll(ScrollView.FOCUS_DOWN)
                    }

                    return true
                }
            })

        }
    }


    // 在 Activity 或 Fragment 中重写 onActivityResult 方法，处理选择文件后的结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                if (uri != null) {
                    requestPost(COMMON_WRITE, TagEntity(TEST_NAME, convertFileToByteArray(uri)))
                } else {
                    ToastUtils.showLong("获取文件失败")
                }
                // 这里可以处理选择的文件 URI，比如获取文件路径等操作
            }
        }
    }

    // 处理权限请求结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了读取外部存储的权限，执行文件读取操作
                pickFile()
            } else {
                ToastUtils.showLong("需要读取外部存储权限才能执行文件读取操作")
                // 用户拒绝了权限请求，可以给出提示信息或其他处理
            }
        }
    }

    // 将文件转换成二进制数据的函数
    fun convertFileToByteArray(uri: Uri): ByteArray? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val byteBuffer = ByteArrayOutputStream()

        inputStream?.use { input ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                byteBuffer.write(buffer, 0, bytesRead)
            }
        }

        return byteBuffer.toByteArray()
    }

    fun tagVersion(view: View) {
        requestGet(TAG_VERSION)
    }

    fun getBaseInfo(view: View) {
        requestGet(GET_BASE_INFO)
    }

    fun getTypeList(view: View) {
        val randomNumber = Random.nextInt(2, 13) // 生成一个范围在 2 到 12 之间的随机数
        requestGet("$GET_TYPE_LIST?typeNumber=$randomNumber")
    }

    fun findFileSize(view: View) {
        requestPost(FIND_FILE_SIZE, TagEntity(TEST_NAME))
    }

    fun addCodeUpRec(view: View) {
        requestPost(ADD_CODE_UP_REC, CodeUpRec(dyNumber = "555"))

    }

    fun encodeAndUpdateEink(view: View) {
        requestPost(ENCODE_AND_UPDATE_EINK, CodeUpRec(dyNumber = "555"))
    }
}