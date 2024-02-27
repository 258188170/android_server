package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.EquMatchDao
import com.card.lp_server.room.entity.EquMatch
import com.card.lp_server.room.repository.IEquMatchRepository

class EquMatchRepositoryImpl(private val equMatch: EquMatchDao) :
    IEquMatchRepository {
    override suspend fun getAll(): List<EquMatch> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): EquMatch? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): EquMatch? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: EquMatch) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: EquMatch) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: EquMatch) {
        TODO("Not yet implemented")
    }

}