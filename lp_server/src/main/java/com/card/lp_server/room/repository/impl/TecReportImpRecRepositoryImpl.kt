package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.TecReportImpRecDao
import com.card.lp_server.room.entity.TecReportImpRec
import com.card.lp_server.room.repository.ITecReportImpRecRepository

class TecReportImpRecRepositoryImpl(private val tecReportImpRecDao: TecReportImpRecDao) :
    ITecReportImpRecRepository {
    override suspend fun getAll(): List<TecReportImpRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): TecReportImpRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): TecReportImpRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: TecReportImpRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: TecReportImpRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: TecReportImpRec) {
        TODO("Not yet implemented")
    }

}