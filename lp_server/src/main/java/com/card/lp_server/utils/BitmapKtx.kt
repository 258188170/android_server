package com.card.lp_server.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log

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