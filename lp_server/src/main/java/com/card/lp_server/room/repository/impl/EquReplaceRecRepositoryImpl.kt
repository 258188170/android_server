package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.EquMatchDao
import com.card.lp_server.room.dao.EquReplaceRecDao
import com.card.lp_server.room.entity.EquReplaceRec
import com.card.lp_server.room.repository.IEquReplaceRecRepository

class EquReplaceRecRepositoryImpl(private val equMatchDao: EquReplaceRecDao) :
    IEquReplaceRecRepository {
    override suspend fun getAll(): List<EquReplaceRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): EquReplaceRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): EquReplaceRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: EquReplaceRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: EquReplaceRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: EquReplaceRec) {
        TODO("Not yet implemented")
    }

}