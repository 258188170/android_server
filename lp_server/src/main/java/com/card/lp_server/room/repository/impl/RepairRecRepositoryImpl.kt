package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.RepairRecDao
import com.card.lp_server.room.entity.RepairRec
import com.card.lp_server.room.repository.IRepairRecRepository

class RepairRecRepositoryImpl(private val repairRecDao: RepairRecDao) :
    IRepairRecRepository {
    override  fun getAll(): List<RepairRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): RepairRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): RepairRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: RepairRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: RepairRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: RepairRec) {
        TODO("Not yet implemented")
    }

}