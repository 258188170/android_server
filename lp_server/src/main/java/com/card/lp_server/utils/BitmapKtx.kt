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



val textArray = arrayOf(
    "导弹型号：%s",
    "导弹弹号：%s",
    "质量等级：%s",
    "军检验收日期：%s",
    "总挂飞架次：%s",
    "总通电时间：%s"
)
val bitmapWidth = 480
val bitmapHeight = 280
val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
fun generateBitMapForLl(recordBean: RecordBean): Bitmap {
    val paint = TextPaint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
        textSize = 15f
    }
    val canvas = Canvas(bitmap)

    val canvasHeight = bitmapHeight
    val totalTextHeight = (6 * paint.fontSpacing).toInt()
    val availableHeight = canvasHeight - 25 * 2
    val verticalPadding = (availableHeight - totalTextHeight) / 2
    val lineHeight = 30

    // Calculate the starting x-coordinate for drawText
    val textX = 20

    val qrImg: Bitmap = createQRCode(recordBean)

    // Calculate the starting x-coordinate for drawBitmap
    val bitmapX = bitmapWidth - 20 - qrImg.width

    // Calculate the starting y-coordinate for both drawText and drawBitmap
    var y = 10 + verticalPadding

    textArray.forEachIndexed { index, format ->
        // Draw text with left margin and vertically centered
        canvas.drawText(
            String.format(format, getValueAtIndex(recordBean, index)),
            textX.toFloat(),
            y.toFloat(),
            paint
        )
        y += lineHeight
    }
    // Calculate the vertical position to center the qrImg
    val qrImgVerticalPosition = (bitmapHeight - qrImg.height) / 2.toFloat()

    // Draw qrImg with right margin and vertically centered
    canvas.drawBitmap(qrImg, bitmapX.toFloat(), qrImgVerticalPosition, paint)

    canvas.save()
    canvas.rotate(90f)
    return bitmap
}

private fun getValueAtIndex(recordBean: RecordBean, index: Int): String {
    return when (index) {
        0 -> recordBean.dyvModel ?: ""
        1 -> recordBean.dyNumber ?: ""
        2 -> recordBean.qualityLevel ?: ""
        3 -> recordBean.acpetDate ?: ""
        4 -> recordBean.upAndDowNum?.toString() ?: ""
        5 -> recordBean.totPwTime?.toString() ?: ""
        else -> ""
    }
}


fun createQRCode(recordBean: RecordBean): Bitmap {
    val toJson = GsonUtils.toJson(recordBean)
    val fromJson = GsonUtils.fromJson(toJson, RecordBean::class.java)
    fromJson.codeUpRecs = null
    fromJson.equMatches = null
    fromJson.equReplaceRecs = null
    fromJson.gasUpRecs = null
    fromJson.gjzbRecs = null
    fromJson.hdRecs = null
    fromJson.importantNotes = null
    fromJson.mtRecs = null
    fromJson.poweronRecs = null
    fromJson.repairRecs = null
    fromJson.sftReplaceRecs = null
    fromJson.tecReportImpRecs = null
    val encode = LPEncodeUtil.getInstance().encode(GsonUtils.toJson(fromJson), 3, 2, 3)
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