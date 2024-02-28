package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.TecReportImpRecDao
import com.card.lp_server.room.entity.TecReportImpRec
import com.card.lp_server.room.repository.ITecReportImpRecRepository

class TecReportImpRecRepositoryImpl(private val tecReportImpRecDao: TecReportImpRecDao) :
    ITecReportImpRecRepository {
    override  fun getAll(): List<TecReportImpRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): TecReportImpRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): TecReportImpRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: TecReportImpRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: TecReportImpRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: TecReportImpRec) {
        TODO("Not yet implemented")
    }

}