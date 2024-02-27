package com.card.lp_server.card.device.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapUtils {

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Log.d("TAG", "rotateBitmap: " + source.getWidth() + source.getHeight());
        int targetWidth = 240; // 目标宽度
        int targetHeight = 440; // 目标高度
        float scaleWidth = (float) targetWidth / source.getWidth();
        float scaleHeight = (float) targetHeight / source.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
    }


}
