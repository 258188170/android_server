package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.HandoverRecDao
import com.card.lp_server.room.entity.HandoverRec
import com.card.lp_server.room.repository.IHandoverRecRepository

class HandoverRecRepositoryImpl(private val handoverRecDao: HandoverRecDao) :
    IHandoverRecRepository {
    override suspend fun getAll(): List<HandoverRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): HandoverRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): HandoverRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: HandoverRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: HandoverRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: HandoverRec) {
        TODO("Not yet implemented")
    }

}