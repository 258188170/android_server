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
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.card.lp_server.card.device.LonbestCard
import com.card.lp_server.model.TagEntity
import com.card.lp_server.nfc.NFCManager
import com.card.lp_server.nfc.nfcAuthIsSuccess
import com.card.lp_server.nfc.nfcReadData
import com.card.lp_server.nfc.nfcWrite
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.entity.EquMatch
import com.card.lp_server.room.entity.EquReplaceRec
import com.card.lp_server.room.entity.GJZBRec
import com.card.lp_server.room.entity.GasUpRec
import com.card.lp_server.room.entity.HandoverRec
import com.card.lp_server.room.entity.ImportantNote
import com.card.lp_server.room.entity.MtRec
import com.card.lp_server.room.entity.PoweronRec
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.room.entity.RepairRec
import com.card.lp_server.room.entity.SorftwareReplaceRec
import com.card.lp_server.room.entity.TecReportImpRec
import com.card.lp_server.server.ConstantsPath.ADD_BASE_INFO
import com.card.lp_server.server.ConstantsPath.ADD_CODE_UP_REC
import com.card.lp_server.server.ConstantsPath.ADD_EQU_MATCH
import com.card.lp_server.server.ConstantsPath.ADD_EQU_REPLACE_REC
import com.card.lp_server.server.ConstantsPath.ADD_GAS_UP_REC
import com.card.lp_server.server.ConstantsPath.ADD_GJZB_REC
import com.card.lp_server.server.ConstantsPath.ADD_HANDOVER_REC
import com.card.lp_server.server.ConstantsPath.ADD_IMPORTANT_NOTE
import com.card.lp_server.server.ConstantsPath.ADD_MT_REC
import com.card.lp_server.server.ConstantsPath.ADD_POWERON_REC
import com.card.lp_server.server.ConstantsPath.ADD_REPAIR_REC
import com.card.lp_server.server.ConstantsPath.ADD_SORFTWARE_REPLACE_REC
import com.card.lp_server.server.ConstantsPath.ADD_TEC_REPORT_IMP_REC
import com.card.lp_server.server.ConstantsPath.CLEAR_TAG
import com.card.lp_server.server.ConstantsPath.COMMON_WRITE
import com.card.lp_server.server.ConstantsPath.DELETE_FILE
import com.card.lp_server.server.ConstantsPath.FIND_FILE_SIZE
import com.card.lp_server.server.ConstantsPath.GET_BASE_INFO
import com.card.lp_server.server.ConstantsPath.GET_TYPE_LIST
import com.card.lp_server.server.ConstantsPath.LIST_FILES
import com.card.lp_server.server.ConstantsPath.READ_FILE
import com.card.lp_server.server.ConstantsPath.TAG_INFO
import com.card.lp_server.server.ConstantsPath.TAG_VERSION
import com.card.lp_server.server.ConstantsPath.UPDATE_DISPLAY

import com.card.lp_server.utils.JSQ1
import com.card.lp_server.utils.convertBitmapToBinary
import com.card.lp_server.utils.generateBitMapForLl
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
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
        private  var TEST_NAME = "履历信息"
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
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.grid(spanCount = 2).divider{
            setDrawable(R.drawable.diver)
            orientation = DividerOrientation.GRID
        }.setup {
            addType<Items>(R.layout.item)
            onClick(R.id.item) {
                when (modelPosition) {
                    0 -> writeFile()
                    1 -> listFiles()
                    2 -> readFile()
                    3 -> deleteFile()
                    4 -> tagInfo()
                    5 -> tagVersion()
                    6 -> clearTag()
                    7 -> testUpdateDisplay()
                    8 -> addBaseInfo()
                    9 -> getBaseInfo()
                    10 -> getTypeList()
                    11 -> findFileSize()
                    12 -> addCodeUpRec()
                    13 -> addEquMatch()
                    14 -> addEquReplaceRec()
                    15 -> addGasUpRec()
                    16 -> addTecReportImpRec()
                    17 -> addSorftwareReplaceRec()
                    18 -> addRepairRec()
                    19 -> addPoweronRec()
                    20 -> addMtRec()
                    21 -> addImportantNote()
                    22 -> addHandoverRec()
                    23 -> addGJZBRec()
                    24 -> jsq()
                    25 -> testnfc()
                }
            }
        }.models = listItems

        val ipAddress = NetworkUtils.getIPAddress(true)
        Log.d(TAG, "onCreate: $ipAddress")
    }

    private fun writeFile() {
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

    fun clearTag() {
        requestGet(CLEAR_TAG)
    }

    fun listFiles() {
        requestGet(LIST_FILES)
    }

    fun deleteFile() {
        requestGet("$DELETE_FILE?fileName=$TEST_NAME")
    }

    fun tagInfo() {
        requestGet(TAG_INFO)
    }

    fun readFile() {
        requestGet("$READ_FILE?fileName=$TEST_NAME")
    }


    fun addBaseInfo() {
        requestPost(ADD_BASE_INFO, RecordBean(dyNumber = "555", display = true))
    }

    fun testUpdateDisplay() {
        val generateBitMapForLl = generateBitMapForLl(RecordBean(dyNumber = "123"))
        val convertBitmapToBinary = convertBitmapToBinary(generateBitMapForLl)
        requestPost(UPDATE_DISPLAY, TagEntity(data = convertBitmapToBinary))


    }


    private fun requestGet(url: String) {
        show("\n\n request: GET path = $url")
        lifecycleScope.launch(Dispatchers.IO) {
            RxHttp.get(url)  //第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()
                .catch {
                    it.printStackTrace()
                }//第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
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
                .toFlow<String>()
                .catch {
                    it.printStackTrace()
                }//第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {
                    dismiss("\n response: path = $url \n result: $it")
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

//                    LonbestCard.getInstance().writeFile(TEST_NAME,convertFileToByteArray(uri))
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
    private fun convertFileToByteArray(uri: Uri): ByteArray? {
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

    fun tagVersion() {
        requestGet(TAG_VERSION)
    }

    fun getBaseInfo() {
        Log.d(TAG, "getBaseInfo: ${Thread.currentThread().name}")
        requestGet(GET_BASE_INFO)
    }

    fun getTypeList() {
        val randomNumber = Random.nextInt(2, 13) // 生成一个范围在 2 到 12 之间的随机数
        requestGet("$GET_TYPE_LIST?typeNumber=$randomNumber")
    }

    fun findFileSize() {
        requestPost(FIND_FILE_SIZE, TagEntity(TEST_NAME))
    }

    fun addCodeUpRec() {
        requestPost(ADD_CODE_UP_REC, CodeUpRec(dyNumber = "555"))

    }


    fun addEquMatch() {
        requestPost(ADD_EQU_MATCH, EquMatch(dyNumber = "555"))
    }

    fun addEquReplaceRec() {
        requestPost(ADD_EQU_REPLACE_REC, EquReplaceRec(dyNumber = "555"))

    }

    fun addGasUpRec() {
        requestPost(ADD_GAS_UP_REC, GasUpRec(dyNumber = "555"))

    }

    fun addTecReportImpRec() {
        requestPost(ADD_TEC_REPORT_IMP_REC, TecReportImpRec(dyNumber = "555"))
    }

    fun addSorftwareReplaceRec() {
        requestPost(ADD_SORFTWARE_REPLACE_REC, SorftwareReplaceRec(dyNumber = "555"))

    }

    fun addRepairRec() {
        requestPost(ADD_REPAIR_REC, RepairRec(dyNumber = "555"))

    }

    fun addPoweronRec() {
        requestPost(ADD_POWERON_REC, PoweronRec(dyNumber = "555"))

    }

    fun addMtRec() {
        requestPost(ADD_MT_REC, MtRec(dyNumber = "555"))

    }

    fun addImportantNote() {
        requestPost(ADD_IMPORTANT_NOTE, ImportantNote(dyNumber = "555"))

    }

    fun addHandoverRec() {
        requestPost(ADD_HANDOVER_REC, HandoverRec(dyNumber = "555"))

    }

    fun addGJZBRec() {
        requestPost(ADD_GJZB_REC, GJZBRec(dyNumber = "555"))
    }

    fun jsq() {
        lifecycleScope.launch(Dispatchers.IO) {
            RxHttp.get("/api/jsq_read")
                .add("jsq", JSQ1)//第一步，确定请求方式，可以选择postForm、postJson等方法
                .toFlow<String>()
                .catch {
                    it.printStackTrace()
                }//第二步，调用toFlow方法并输入泛型类型，拿到Flow对象
                .collect {              //第三步，调用collect方法发起请求
                    withContext(Dispatchers.Main) {
                        LogUtils.d(it)
                        dismiss("\n response: path = api/jsq_read \n result: $it")
                    }
                }

        }
    }

    override fun onResume() {
        super.onResume()
        NFCManager.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        NFCManager.onPause(this)
    }

    fun testnfc() {
        Log.d(TAG, "nfc: 开始")
        NFCManager.startSession {
            val nfcAuthIsSuccess = it.nfcAuthIsSuccess()
            Log.d(TAG, "nfc: $nfcAuthIsSuccess")
            if (nfcAuthIsSuccess) {
                val toJson = GsonUtils.toJson(RecordBean())
                val nfcWrite = it.nfcWrite(toJson.toByteArray())

                Log.d(TAG, "testnfc: nfcWrite-->$nfcWrite")
                val nfcReadData = it.nfcReadData(0, toJson.toByteArray().size)
                if (nfcReadData != null) {
                    Log.d(TAG, "testnfc: ${ConvertUtils.bytes2String(nfcReadData)}")
                }
            }
            NFCManager.stopSession()
        }
    }
}