package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.ImportantNoteDao
import com.card.lp_server.room.entity.ImportantNote
import com.card.lp_server.room.repository.ImportantNoteRepository

class ImportantNoteRepositoryImpl(private val importantNoteDao: ImportantNoteDao) :
    ImportantNoteRepository {
    override  fun getAll(): List<ImportantNote> {
        TODO("Not yet implemented")
    }

    override  fun loadByNumber(dyNumber: String): ImportantNote? {
        TODO("Not yet implemented")
    }

    override  fun loadById(id: Int): ImportantNote? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: ImportantNote) {
        TODO("Not yet implemented")
    }

    override  fun deleteItem(t: ImportantNote) {
        TODO("Not yet implemented")
    }

    override  fun updateItem(t: ImportantNote) {
        TODO("Not yet implemented")
    }

}