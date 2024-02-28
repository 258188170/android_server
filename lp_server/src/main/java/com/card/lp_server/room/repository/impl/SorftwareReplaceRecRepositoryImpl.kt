package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.SorftwareReplaceRecDao
import com.card.lp_server.room.entity.SorftwareReplaceRec
import com.card.lp_server.room.repository.ISorftwareReplaceRecRepository

class SorftwareReplaceRecRepositoryImpl(private val sorftwareReplaceRecDao: SorftwareReplaceRecDao) :
    ISorftwareReplaceRecRepository {
    override  fun getAll(): List<SorftwareReplaceRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): SorftwareReplaceRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): SorftwareReplaceRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: SorftwareReplaceRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: SorftwareReplaceRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: SorftwareReplaceRec) {
        TODO("Not yet implemented")
    }

}