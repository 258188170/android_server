package com.card.lp_server

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.card.lp_server.room.AppContainer
import com.card.lp_server.room.AppDataContainer
import com.card.lp_server.server.LServer

object ServerSingleton {
    // 单例的属性和方法
    lateinit var container: AppContainer
    val lpServer by lazy {
        LServer(9988)
    }

    fun init(context: Context) {
        container = AppDataContainer(context.applicationContext)
        try {
            lpServer.start()
            LogUtils.i("启动成功!")
        } catch (e: Exception) {
            LogUtils.e("启动失败-->>${e.message}")
        }
    }

}