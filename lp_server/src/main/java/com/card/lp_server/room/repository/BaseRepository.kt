package com.card.lp_server.room.repository

import com.card.lp_server.room.entity.RecordBean

interface BaseRepository<T> {

    suspend fun getAll(): List<T>

    /**
     * Retrieve an item from the given data source that matches with the [dyNumber].
     */
    suspend fun loadByNumber(dyNumber: String): T?

    /**
     * Insert item in the data source
     */
     suspend fun insertItem(t: T)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(t: T)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(t: T)
}