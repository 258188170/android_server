package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.RepairRecDao
import com.card.lp_server.room.entity.RepairRec
import com.card.lp_server.room.repository.IRepairRecRepository

class RepairRecRepositoryImpl(private val repairRecDao: RepairRecDao) :
    IRepairRecRepository {
    override suspend fun getAll(): List<RepairRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): RepairRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): RepairRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: RepairRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: RepairRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: RepairRec) {
        TODO("Not yet implemented")
    }

}