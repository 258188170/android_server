package com.card.lp_server.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.card.lp_server.room.dao.CodeUpRecDao
import com.card.lp_server.room.dao.EquMatchDao
import com.card.lp_server.room.dao.EquReplaceRecDao
import com.card.lp_server.room.dao.GJZBRecDao
import com.card.lp_server.room.dao.GasUpRecDao
import com.card.lp_server.room.dao.HandoverRecDao
import com.card.lp_server.room.dao.ImportantNoteDao
import com.card.lp_server.room.dao.MtRecDao
import com.card.lp_server.room.dao.PoweronRecDao
import com.card.lp_server.room.dao.RecordBeanDao
import com.card.lp_server.room.dao.RepairRecDao
import com.card.lp_server.room.dao.SorftwareReplaceRecDao
import com.card.lp_server.room.dao.TecReportImpRecDao
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

/**
 * Database class with a singleton Instance object.
 */
@Database(
    entities = [
        RecordBean::class,
        CodeUpRec::class,
        EquMatch::class,
        EquReplaceRec::class,
        GasUpRec::class,
        GJZBRec::class,
        HandoverRec::class,
        ImportantNote::class,
        MtRec::class,
        PoweronRec::class,
        RepairRec::class,
        SorftwareReplaceRec::class,
        TecReportImpRec::class,

    ], version = 1, exportSchema = false
)
abstract class AppDataDatabase : RoomDatabase() {
    abstract fun recordBeanDao(): RecordBeanDao
    abstract fun codeUpRecDao(): CodeUpRecDao
    abstract fun equReplaceRecDao(): EquReplaceRecDao
    abstract fun gasUpRecDao(): GasUpRecDao
    abstract fun gJZBRecDao(): GJZBRecDao
    abstract fun handoverRecDao(): HandoverRecDao
    abstract fun importantNoteDao(): ImportantNoteDao
    abstract fun mtRecDao(): MtRecDao
    abstract fun poweronRecDao(): PoweronRecDao
    abstract fun repairRecDao(): RepairRecDao
    abstract fun sorftwareReplaceRecDao(): SorftwareReplaceRecDao
    abstract fun tecReportImpRecDao(): TecReportImpRecDao

    companion object {
        @Volatile
        private var Instance: AppDataDatabase? = null
        fun getDatabase(context: Context): AppDataDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDataDatabase::class.java, "lp_server_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}