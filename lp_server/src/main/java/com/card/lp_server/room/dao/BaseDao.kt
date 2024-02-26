package com.card.lp_server.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insert(vararg obj: T)
    @Delete
     fun remove(vararg obj: T)
    @Update
     fun update(vararg obj: T)


}