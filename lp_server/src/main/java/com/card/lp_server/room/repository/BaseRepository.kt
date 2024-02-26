package com.card.lp_server.room.repository

import com.card.lp_server.room.entity.RecordBean

interface BaseRepository<T> {

    fun getAll(): List<T>

    /**
     * Retrieve an item from the given data source that matches with the [dyNumber].
     */
    fun loadByNumber(dyNumber: String): T?

    /**
     * Insert item in the data source
     */
     fun insertItem(t: T)

    /**
     * Delete item from the data source
     */
     fun deleteItem(t: T)

    /**
     * Update item in the data source
     */
     fun updateItem(t: T)
}