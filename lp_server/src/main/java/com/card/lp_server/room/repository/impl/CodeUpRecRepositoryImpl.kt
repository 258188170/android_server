package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.CodeUpRecDao
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.repository.ICodeUpRecRepository


class CodeUpRecRepositoryImpl(private val codeUpRec: CodeUpRecDao) :
    ICodeUpRecRepository {
    override  fun getAll(): List<CodeUpRec> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): CodeUpRec? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): CodeUpRec? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: CodeUpRec) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: CodeUpRec) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: CodeUpRec) {
        TODO("Not yet implemented")
    }

}













