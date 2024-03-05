package com.card.lp_server

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.card.lp_server.card.HIDCommunicationUtil
import com.card.lp_server.room.AppContainer
import com.card.lp_server.room.AppDataContainer
import com.card.lp_server.server.LServer

val mAppContext: Application by lazy { Ktx.app }

val mAppContainer: AppContainer by lazy {
    AppDataContainer()
}
val mLpServer by lazy {
    LServer()
}

class Ktx : ContentProvider() {
    companion object {
        lateinit var app: Application
    }

    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        install(application)
        return true
    }

    private fun install(application: Application) {
        app = application
        try {
            mLpServer.start()
            LogUtils.i("启动成功!")
            HIDCommunicationUtil.instance.registerUSBReceiver()
        } catch (e: Exception) {
            LogUtils.e("启动失败-->>${e.message}")
        }
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}