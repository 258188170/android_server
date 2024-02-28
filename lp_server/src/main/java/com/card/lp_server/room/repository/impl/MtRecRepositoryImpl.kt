package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.MtRecDao
import com.card.lp_server.room.entity.MtRec
import com.card.lp_server.room.repository.IMtRecRepository

class MtRecRepositoryImpl(private val mtRecDao: MtRecDao) :
    IMtRecRepository {
    override  fun getAll(): List<MtRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): MtRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): MtRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: MtRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: MtRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: MtRec) {
        TODO("Not yet implemented")
    }

}