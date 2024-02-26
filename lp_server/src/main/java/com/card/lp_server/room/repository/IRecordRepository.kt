package com.card.lp_server.room.repository

import com.card.lp_server.room.dao.RecordBeanDao
import com.card.lp_server.room.entity.RecordBean


class IRecordRepository(private val recordBeanDao: RecordBeanDao) : RecordRepository {
    override fun getAll(): List<RecordBean> {
        return recordBeanDao.getAll()
    }

    override fun loadByNumber(dyNumber: String): RecordBean? {
        return recordBeanDao.loadById(dyNumber)
    }

    override fun insertItem(t: RecordBean) {
        return recordBeanDao.insert(t)
    }

    override fun deleteItem(t: RecordBean) {
        return recordBeanDao.remove(t)
    }

    override fun updateItem(t: RecordBean) {
        return recordBeanDao.update(t)
    }

}