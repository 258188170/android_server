package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.HandoverRecDao
import com.card.lp_server.room.entity.HandoverRec
import com.card.lp_server.room.repository.IHandoverRecRepository

class HandoverRecRepositoryImpl(private val handoverRecDao: HandoverRecDao) :
    IHandoverRecRepository {
    override  fun getAll(): List<HandoverRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): HandoverRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): HandoverRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: HandoverRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: HandoverRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: HandoverRec) {
        TODO("Not yet implemented")
    }

}