package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.GasUpRecDao
import com.card.lp_server.room.entity.GasUpRec
import com.card.lp_server.room.repository.IGasUpRecRepository

class GasUpRecRepositoryImpl(private val gasUpRecDao: GasUpRecDao) :
    IGasUpRecRepository {
    override suspend fun getAll(): List<GasUpRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): GasUpRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): GasUpRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: GasUpRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: GasUpRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: GasUpRec) {
        TODO("Not yet implemented")
    }

}