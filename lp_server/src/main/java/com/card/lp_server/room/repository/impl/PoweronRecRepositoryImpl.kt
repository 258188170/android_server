package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.PoweronRecDao
import com.card.lp_server.room.entity.PoweronRec
import com.card.lp_server.room.repository.IPoweronRecRepository

class PoweronRecRepositoryImpl(private val poweronRecDao: PoweronRecDao) :
    IPoweronRecRepository {
    override suspend fun getAll(): List<PoweronRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): PoweronRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): PoweronRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: PoweronRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: PoweronRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: PoweronRec) {
        TODO("Not yet implemented")
    }

}