package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.GJZBRecDao
import com.card.lp_server.room.entity.GJZBRec
import com.card.lp_server.room.repository.IGJZBRecRepository

class GJZBRecRepositoryImpl(private val gjzbRecDao: GJZBRecDao) :
    IGJZBRecRepository {
    override suspend fun getAll(): List<GJZBRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): GJZBRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): GJZBRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: GJZBRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: GJZBRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: GJZBRec) {
        TODO("Not yet implemented")
    }

}