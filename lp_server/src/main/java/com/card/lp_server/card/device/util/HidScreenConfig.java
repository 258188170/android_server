package com.card.lp_server.card.device.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HidScreenConfig {


    public static byte[] clearScreen(){
        Bitmap bitmap = Bitmap.createBitmap(480, 280, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.rotate(90);
        return  bitmapToByteArray(bitmap);
    }


    public static byte[] bitmapToByteArray( Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // 可以选择不同的格式和压缩质量
        return stream.toByteArray();
    }
    //旋转 bitmap
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Log.d("TAG", "rotateBitmap: " + source.getWidth() + source.getHeight());
        int targetWidth = 250; // 目标宽度
        int targetHeight = 250; // 目标高度
        float scaleWidth = (float) targetWidth / source.getWidth();
        float scaleHeight = (float) targetHeight / source.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
    }

    public static byte[] convertBitmapToBinary(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preRotate(270f);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        ByteArrayOutputStream baios = new ByteArrayOutputStream();
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int color = bitmap1.getPixel(j, i);
                int alpha = ((color & 0xFF000000) >> 24); // 透明度
                int red = ((color & 0x00FF0000) >> 16);   // 红色
                int green = ((color & 0x0000FF00) >> 8);  // 绿色
                int blue = (color & 0x000000FF);          // 蓝色
                color = (int) (red * 0.3 + green * 0.59 + blue * 0.11);  // 转化为灰度图  灰度值：255为白色，0为黑色
                byte temp = color > 127 ? (byte) 1 : 0;
                if (alpha == 0) {
                    temp = (byte) 1;
                }
                data.append(temp);
                if (data.length() == 8) {
                    byte[] content1 = new byte[1];
                    content1[0] = (byte) Integer.parseInt(data.toString(), 2);
                    try {
                        baios.write(content1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    data = new StringBuilder();
                }
            }
        }
        Log.d("TAG", "covertBitmap: "+baios.size());
        return baios.toByteArray();
    }


    public static byte[] convertBitmapToBinaryNew(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preRotate(270f);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 计算字节数组的大小
        int dataSize = width * height;
        int byteArraySize = (dataSize + 7) / 8;  // 向上取整，确保字节数组足够存储所有像素数据

        byte[] byteArray = new byte[byteArraySize];

        int index = 0;

        // 遍历每个像素，并转换为黑白（二值化）的灰度值存储在字节数组中
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = bitmap1.getPixel(x, y);
                int alpha = Color.alpha(color);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                // 计算灰度值
                int gray = (int) (red * 0.3 + green * 0.59 + blue * 0.11);

                // 将灰度值转换为黑白（二值化）
                byte byteValue = (byte) (gray > 127 ? 1 : 0);
                // byte byteValue;
                if (alpha == 0) {
                    byteValue = (byte) 1;
                }
                // 计算在字节数组中的索引和位偏移
                int byteIndex = index / 8;
                int bitOffset = index % 8;

                // 将二进制位设置到字节数组中
                byteArray[byteIndex] |= (byteValue << (7 - bitOffset));

                index++;
            }
        }
        Log.d("TAG", "convertBitmapToBinary: " + byteArray.length);
        return byteArray;
    }

}
