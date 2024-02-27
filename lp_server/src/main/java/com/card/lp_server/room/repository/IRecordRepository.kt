package com.card.lp_server.room.repository

import com.card.lp_server.room.dao.RecordBeanDao
import com.card.lp_server.room.entity.RecordBean


class IRecordRepository(private val recordBeanDao: RecordBeanDao) : RecordRepository {
    override suspend fun getAll(): List<RecordBean> {
        return recordBeanDao.getAll()
    }

    override suspend fun loadByNumber(dyNumber: String): RecordBean? {
        return recordBeanDao.loadById(dyNumber)
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