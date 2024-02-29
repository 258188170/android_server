package com.card.lp_server.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.text.TextPaint
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.TimeUtils
import com.card.lp_server.room.entity.RecordBean


fun generateBitMapForLl(): Bitmap {
    val mCurrentPaint = TextPaint()
    mCurrentPaint.color = Color.BLACK
    mCurrentPaint.textAlign = Paint.Align.LEFT
    mCurrentPaint.textSize = 15f
    val bitmap = Bitmap.createBitmap(480, 280, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val zbxhmc = "长剑-20导弹"
    val dybh = "CJ2390007"
    val zldj = "新品"
    val ccrq = "2023-10-12"
    canvas.drawText("弹药型号：$zbxhmc", 25f, (70 + 20).toFloat(), mCurrentPaint)
    canvas.drawText("弹药编号：$dybh", 25f, (110 + 20).toFloat(), mCurrentPaint)
    canvas.drawText("质量等级：$zldj", 25f, (150 + 20).toFloat(), mCurrentPaint)
    canvas.drawText("军检验收日期：$ccrq", 25f, (190 + 20).toFloat(), mCurrentPaint)
    val time = TimeUtils.getNowString()
    Log.d("TAG", "generateBitMapForLltest: 更新时间$time")
    val qrImg = createQRCode("$zbxhmc#$dybh#$zldj#$ccrq#$time#", 180)
    canvas.drawBitmap(qrImg, 230f, 20f, mCurrentPaint)
    canvas.save()
    canvas.rotate(90f)
    return bitmap
}


fun generateBitMapForLl(recordBean: RecordBean): Bitmap {
    val mCurrentPaint = TextPaint()
    mCurrentPaint.color = Color.BLACK
    mCurrentPaint.textAlign = Paint.Align.LEFT
    mCurrentPaint.textSize = 15f
    val bitmap = Bitmap.createBitmap(480, 280, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val x = 25 // 文本的左边距
    val canvasHeight = 280 // 画布的高度
    val totalTextHeight = (6 * mCurrentPaint.fontSpacing).toInt() // 6 行文本的总高度
    val availableHeight = canvasHeight - 25 * 2 // 可用的垂直高度，减去顶部和底部各 25 像素
    val verticalPadding = (availableHeight - totalTextHeight) / 2 // 垂直内边距，使文本在可用的高度内上下居中
    var y = 10 + verticalPadding // 加上顶部的 25 像素
    val lineHeight = 30 // 每行文本之间的间距
    canvas.drawText("导弹型号：${recordBean.dyvModel}", x.toFloat(), y.toFloat(), mCurrentPaint)
    y += lineHeight
    canvas.drawText("导弹弹号：${recordBean.dyNumber}", x.toFloat(), y.toFloat(), mCurrentPaint)
    y += lineHeight
    canvas.drawText("质量等级：${recordBean.qualityLevel}", x.toFloat(), y.toFloat(), mCurrentPaint)
    y += lineHeight
    canvas.drawText("军检验收日期：${recordBean.acpetDate}", x.toFloat(), y.toFloat(), mCurrentPaint)
    y += lineHeight
    canvas.drawText("总挂飞架次：${recordBean.upAndDowNum}", x.toFloat(), y.toFloat(), mCurrentPaint)
    y += lineHeight
    canvas.drawText("总通电时间：${recordBean.totPwTime}", x.toFloat(), y.toFloat(), mCurrentPaint)
    val qrImg: Bitmap = createQRCode(
        GsonUtils.toJson(recordBean),
        180,
    )
    canvas.drawBitmap(qrImg, 230f, 20f, mCurrentPaint)
    canvas.save()
    canvas.rotate(90f)
    return bitmap
}


fun createQRCode(str: String?, widthAndHeight: Int): Bitmap {
    val encode = LPEncodeUtil.getInstance().encode(str, 3, 5, 5)
    return BitmapFactory.decodeByteArray(encode, 0, encode.size)
}


fun generateBitMapForLlFormat(): Bitmap {
    val bitmap = Bitmap.createBitmap(480, 280, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.save()
    canvas.rotate(90f)
    return bitmap
}


fun convertBitmapToBinary(bitmap: Bitmap): ByteArray {
    val matrix = Matrix()
    matrix.preRotate(270f)
    val bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    val width = bitmap1.width
    val height = bitmap1.height
    // 计算字节数组的大小
    val dataSize = width * height
    val byteArraySize = (dataSize + 7) / 8 // 向上取整，确保字节数组足够存储所有像素数据
    val byteArray = ByteArray(byteArraySize)
    var index = 0
    // 遍历每个像素，并转换为黑白（二值化）的灰度值存储在字节数组中
    for (y in 0 until height) {
        for (x in 0 until width) {
            val color = bitmap1.getPixel(x, y)
            val alpha = Color.alpha(color)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            // 计算灰度值
            val gray = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()

            // 将灰度值转换为黑白（二值化）
            var byteValue = (if (gray > 127) 1 else 0).toByte()
            // byte byteValue;
            if (alpha == 0) {
                byteValue = 1.toByte()
            }
            // 计算在字节数组中的索引和位偏移
            val byteIndex = index / 8
            val bitOffset = index % 8

            // 将二进制位设置到字节数组中
            byteArray[byteIndex] =
                (byteArray[byteIndex].toInt() or (byteValue.toInt() shl 7 - bitOffset)).toByte()
            index++
        }
    }
    return byteArray
}