package com.card.lp_server.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
interface RecordBeanDao:BaseDao<RecordBean> {
    @Query("SELECT * FROM recordbean")
     fun getAll(): List<RecordBean>

    @Query("SELECT * FROM recordbean WHERE id IN (:ids)")
     fun loadAllByIds(ids: IntArray): List<RecordBean>

    @Query("SELECT * FROM recordbean WHERE dyNumber = (:dyNumber)")
     fun loadById(dyNumber: String): RecordBean?
    @Query("SELECT * FROM recordbean WHERE dyNumber LIKE :first AND " +
            "dyvModel LIKE :last LIMIT 1")
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
interface CodeUpRecDao:BaseDao<CodeUpRec> {

}
@Dao
interface EquMatchDao:BaseDao<EquMatch> {

}
@Dao
interface EquReplaceRecDao:BaseDao<EquReplaceRec> {

}
@Dao
interface GasUpRecDao:BaseDao<GasUpRec> {

}
@Dao
interface GJZBRecDao:BaseDao<GJZBRec> {

}
@Dao
interface HandoverRecDao:BaseDao<HandoverRec> {

}
@Dao
interface ImportantNoteDao:BaseDao<ImportantNote> {

}
@Dao
interface MtRecDao:BaseDao<MtRec> {

}
@Dao
interface PoweronRecDao:BaseDao<PoweronRec> {

}
@Dao
interface RepairRecDao:BaseDao<RepairRec> {

}
@Dao
interface SorftwareReplaceRecDao:BaseDao<SorftwareReplaceRec> {

}
@Dao
interface TecReportImpRecDao:BaseDao<TecReportImpRec> {

}