package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.EquMatchDao
import com.card.lp_server.room.entity.EquMatch
import com.card.lp_server.room.repository.IEquMatchRepository

class EquMatchRepositoryImpl(private val equMatch: EquMatchDao) :
    IEquMatchRepository {
    override  fun getAll(): List<EquMatch> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): EquMatch? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): EquMatch? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: EquMatch) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: EquMatch) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: EquMatch) {
        TODO("Not yet implemented")
    }

}