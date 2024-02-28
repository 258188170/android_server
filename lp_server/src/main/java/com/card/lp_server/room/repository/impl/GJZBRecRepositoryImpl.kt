package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.GJZBRecDao
import com.card.lp_server.room.entity.GJZBRec
import com.card.lp_server.room.repository.IGJZBRecRepository

class GJZBRecRepositoryImpl(private val gjzbRecDao: GJZBRecDao) :
    IGJZBRecRepository {
    override  fun getAll(): List<GJZBRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): GJZBRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): GJZBRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: GJZBRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: GJZBRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: GJZBRec) {
        TODO("Not yet implemented")
    }

}