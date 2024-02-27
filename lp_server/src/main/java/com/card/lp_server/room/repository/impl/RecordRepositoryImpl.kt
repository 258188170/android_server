package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.RecordBeanDao
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.room.repository.IRecordRepository


class RecordRepositoryImpl(private val recordBeanDao: RecordBeanDao) :
    IRecordRepository {
    override suspend fun getAll(): List<RecordBean> {
        return recordBeanDao.getAll()
    }

    override suspend fun loadByNumber(dyNumber: String): RecordBean? {
        return recordBeanDao.loadById(dyNumber)
    }

    override suspend fun loadById(id: Int): RecordBean? {
        TODO("Not yet implemented")
    }

    override suspend fun insertItem(t: RecordBean) {
        return recordBeanDao.insert(t)
    }

    override suspend fun deleteItem(t: RecordBean) {
        return recordBeanDao.remove(t)
    }

    override suspend fun updateItem(t: RecordBean) {
        return recordBeanDao.update(t)
    }

}