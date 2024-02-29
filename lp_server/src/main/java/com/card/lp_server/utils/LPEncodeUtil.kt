package com.card.lp_server.utils

import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ResourceUtils
import com.lpcode.encoding.v0048.LPEncode
import java.io.File
import kotlin.concurrent.Volatile

/**
 * 龙贝码生成工具
 */
class LPEncodeUtil private constructor() {
    private var lpEncode: LPEncode? = null

    init {
        //--------------/data/data/com.lpcode.www.store/files/lpcode0025.dat
        val file = File(ActivityUtils.getTopActivity().filesDir, "lpcode0048.dat")
        if (!file.exists()) {
            try {
                ResourceUtils.copyFileFromAssets("lpcode/lpcode0048.dat", file.path)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        if (file.exists()) {
            lpEncode = LPEncode()
            lpEncode?.SetRegisterPath(file.parent)
            Log.i(TAG, "LPEncodeUtil: 注册文件已存在")
        } else {
            Log.i(TAG, "LPEncodeUtil: 注册文件不存在,或者没有读取权限")
            throw RuntimeException("LPEncodeUtil: 注册文件不存在,或者没有读取权限" + file.absolutePath)
        }
    }

    /**
     * byte[] LPEncode::EncodeStrData(byte[] lpdata, byte[] qrdata, int lpecc, int qrecc, int rownum, int cellsize, int gap,int imageformat)
     * lpdata是龙贝码编码字符字节数组，qrdata是QR码编码字符字节数组
     * lpecc是龙贝码纠错级别，qrecc是QR码纠错级别，从低到高依次设置为0-3，qrecc设小于0的数时,表示不生成QR码
     * rownum是条码行数，cellsize是单元大小，path是生成的jpeg文件路径
     * rownum是正数的时候rownum%10000是行数，rownum/10000是列数，rownum%10000为零表示行数不固定，列数设为固定值，
     * rownum/10000为零表示列数不固定，行数设为固定值，列数要大于等于13，行数要大于等于11
     * 负数的时候设置长宽比，千位和百
     * 位是分子，十位和个位是分母，例如-abcd表示列和行的比为ab/cd
     * gap是码图留白
     * imageformat 0是bmp,1是jpg
     * 返回图片字节流
     */
    fun encode(string: String?, ecc: Int, size: Int, gap: Int): ByteArray {
        Log.d(TAG, "encode222222: $string")
        return lpEncode!!.EncodeStrData(string, null, "UTF-8", ecc, 0, -101, size, gap)
    }

    companion object {
        private const val TAG = "LPEncodeUtil"

        @Volatile
        private var instance: LPEncodeUtil? = null

        fun getInstance(): LPEncodeUtil {
            return instance ?: synchronized(this) {
                instance ?: LPEncodeUtil().also { instance = it }
            }
        }
    }
}
