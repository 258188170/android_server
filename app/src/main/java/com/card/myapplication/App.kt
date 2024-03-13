package com.card.myapplication

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.mLpServer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init()
        /**
         * 启动 http 服务
         */
//        try {
//            mLpServer.start()
//            LogUtils.i("启动成功!")
//
//        } catch (e: Exception) {
//            LogUtils.e("启动失败-->>${e.message}")
//        }
    }
}