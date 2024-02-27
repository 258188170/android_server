package com.card.lp_server.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.card.lp_server.base.BaseDao
import com.card.lp_server.room.entity.CodeUpRec
import com.card.lp_server.room.entity.EquMatch
import com.card.lp_server.room.entity.EquReplaceRec
import com.card.lp_server.room.entity.GJZBRec
import com.card.lp_server.room.entity.GasUpRec
import com.card.lp_server.room.entity.HandoverRec
import com.card.lp_server.room.entity.ImportantNote
import com.card.lp_server.room.entity.MtRec
import com.card.lp_server.room.entity.PoweronRec
import com.card.lp_server.room.entity.RecordBean
import com.card.lp_server.room.entity.RepairRec
import com.card.lp_server.room.entity.SorftwareReplaceRec
import com.card.lp_server.room.entity.TecReportImpRec

@Dao
interface RecordBeanDao : BaseDao<RecordBean> {
    @Query("SELECT * FROM recordbean")
    fun getAll(): List<RecordBean>

    @Query("SELECT * FROM recordbean WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<RecordBean>

    @Query("SELECT * FROM recordbean WHERE dyNumber = (:dyNumber)")
    fun loadById(dyNumber: String): RecordBean?

    @Query(
        "SELECT * FROM recordbean WHERE dyNumber LIKE :first AND " +
                "dyvModel LIKE :last LIMIT 1"
    )
    fun findByName(first: String, last: String): RecordBean

    @Insert
    fun insertAll(list: List<RecordBean>)

    @Transaction
    fun updateData(users: List<RecordBean>) {
//        getAll()
//        insertAll()
    }

}

@Dao
interface CodeUpRecDao : BaseDao<CodeUpRec> {
    @Query("SELECT * FROM codeuprec")
    fun getAll(): List<CodeUpRec>
}

@Dao
interface EquMatchDao : BaseDao<EquMatch> {
    @Query("SELECT * FROM equmatch")
    fun getAll(): List<EquMatch>
}

@Dao
interface EquReplaceRecDao : BaseDao<EquReplaceRec> {
    @Query("SELECT * FROM equReplaceRec")
    fun getAll(): List<EquReplaceRec>
}

@Dao
interface GasUpRecDao : BaseDao<GasUpRec> {
    @Query("SELECT * FROM gasuprec")
    fun getAll(): List<GasUpRec>
}

@Dao
interface GJZBRecDao : BaseDao<GJZBRec> {
    @Query("SELECT * FROM gjzbrec")
    fun getAll(): List<GJZBRec>
}

@Dao
interface HandoverRecDao : BaseDao<HandoverRec> {
    @Query("SELECT * FROM handoverRec")
    fun getAll(): List<HandoverRec>
}

@Dao
interface ImportantNoteDao : BaseDao<ImportantNote> {
    @Query("SELECT * FROM importantNote")
    fun getAll(): List<ImportantNote>
}

@Dao
interface MtRecDao : BaseDao<MtRec> {
    @Query("SELECT * FROM mtRec")
    fun getAll(): List<MtRec>
}

@Dao
interface PoweronRecDao : BaseDao<PoweronRec> {
    @Query("SELECT * FROM poweronRec")
    fun getAll(): List<PoweronRec>
}

@Dao
interface RepairRecDao : BaseDao<RepairRec> {
    @Query("SELECT * FROM repairRec")
    fun getAll(): List<RepairRec>
}

@Dao
interface SorftwareReplaceRecDao : BaseDao<SorftwareReplaceRec> {
    @Query("SELECT * FROM sorftwareReplaceRec")
    fun getAll(): List<SorftwareReplaceRec>
}

@Dao
interface TecReportImpRecDao : BaseDao<TecReportImpRec> {
    @Query("SELECT * FROM tecReportImpRec")
    fun getAll(): List<TecReportImpRec>
}