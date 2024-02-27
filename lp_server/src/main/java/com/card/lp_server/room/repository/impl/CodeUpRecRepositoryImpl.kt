package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.CodeUpRecDao
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.repository.ICodeUpRecRepository


class CodeUpRecRepositoryImpl(private val codeUpRec: CodeUpRecDao) :
    ICodeUpRecRepository {
    override suspend fun getAll(): List<CodeUpRec> {
        TODO("Not yet implemented")
    }

    override suspend fun loadByNumber(dyNumber: String): CodeUpRec? {
        TODO("Not yet implemented")
    }

    override suspend fun loadById(id: Int): CodeUpRec? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: CodeUpRec) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(t: CodeUpRec) {
        TODO("Not yet implemented")
    }

    override suspend fun updateItem(t: CodeUpRec) {
        TODO("Not yet implemented")
    }

}













