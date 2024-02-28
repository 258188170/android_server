package com.card.lp_server.room.repository.impl

import com.card.lp_server.room.dao.RecordBeanDao
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.room.repository.IRecordRepository


class RecordRepositoryImpl(private val recordBeanDao: RecordBeanDao) :
    IRecordRepository {
    override  fun getAll(): List<RecordBean> {
        return recordBeanDao.getAll()
    }

    override  fun loadByNumber(dyNumber: String): RecordBean? {
        return recordBeanDao.loadById(dyNumber)
    }

    override  fun loadById(id: Int): RecordBean? {
        TODO("Not yet implemented")
    }

    override  fun insertItem(t: RecordBean) {
        return recordBeanDao.insert(t)
    }

    override  fun deleteItem(t: RecordBean) {
        return recordBeanDao.remove(t)
    }

    override  fun updateItem(t: RecordBean) {
        return recordBeanDao.update(t)
    }

}