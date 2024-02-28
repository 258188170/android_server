package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.GasUpRecDao
import com.card.lp_server.room.entity.GasUpRec
import com.card.lp_server.room.repository.IGasUpRecRepository

class GasUpRecRepositoryImpl(private val gasUpRecDao: GasUpRecDao) :
    IGasUpRecRepository {
    override  fun getAll(): List<GasUpRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): GasUpRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): GasUpRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: GasUpRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: GasUpRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: GasUpRec) {
        TODO("Not yet implemented")
    }

}