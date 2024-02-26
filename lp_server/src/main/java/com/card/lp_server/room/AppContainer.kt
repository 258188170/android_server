package com.card.lp_server.room

import android.content.Context
import com.card.lp_server.room.repository.IRecordRepository
import com.card.lp_server.room.repository.RecordRepository

interface AppContainer {
    val recordRepository: RecordRepository
}

/**
 * [AppContainer] implementation that provides instance of [IRecordRepository]
 */
class AppDataContainer(
    private val context: Context

) : AppContainer {
    override val recordRepository: RecordRepository by lazy {
        IRecordRepository(AppDataDatabase.getDatabase(context).recordBeanDao())
    }

}