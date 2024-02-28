package com.card.lp_server.room

import com.card.lp_server.room.repository.ICodeUpRecRepository
import com.card.lp_server.room.repository.IEquMatchRepository
import com.card.lp_server.room.repository.IEquReplaceRecRepository
import com.card.lp_server.room.repository.IGJZBRecRepository
import com.card.lp_server.room.repository.IGasUpRecRepository
import com.card.lp_server.room.repository.IHandoverRecRepository
import com.card.lp_server.room.repository.IMtRecRepository
import com.card.lp_server.room.repository.IPoweronRecRepository
import com.card.lp_server.room.repository.IRecordRepository
import com.card.lp_server.room.repository.IRepairRecRepository
import com.card.lp_server.room.repository.ISorftwareReplaceRecRepository
import com.card.lp_server.room.repository.ITecReportImpRecRepository
import com.card.lp_server.room.repository.ImportantNoteRepository
import com.card.lp_server.room.repository.impl.CodeUpRecRepositoryImpl
import com.card.lp_server.room.repository.impl.EquMatchRepositoryImpl
import com.card.lp_server.room.repository.impl.EquReplaceRecRepositoryImpl
import com.card.lp_server.room.repository.impl.GJZBRecRepositoryImpl
import com.card.lp_server.room.repository.impl.GasUpRecRepositoryImpl
import com.card.lp_server.room.repository.impl.HandoverRecRepositoryImpl
import com.card.lp_server.room.repository.impl.ImportantNoteRepositoryImpl
import com.card.lp_server.room.repository.impl.MtRecRepositoryImpl
import com.card.lp_server.room.repository.impl.PoweronRecRepositoryImpl
import com.card.lp_server.room.repository.impl.RecordRepositoryImpl
import com.card.lp_server.room.repository.impl.RepairRecRepositoryImpl
import com.card.lp_server.room.repository.impl.SorftwareReplaceRecRepositoryImpl
import com.card.lp_server.room.repository.impl.TecReportImpRecRepositoryImpl

interface AppContainer {
    val mRecordRepository: IRecordRepository
    val mEquMatchRepository: IEquMatchRepository
    val mEquReplaceRecRepository: IEquReplaceRecRepository
    val mGasUpRecRepository: IGasUpRecRepository
    val mGJZBRecRepository: IGJZBRecRepository
    val mHandoverRecRepository: IHandoverRecRepository
    val mImportantNoteRepository: ImportantNoteRepository
    val mMtRecRepository: IMtRecRepository
    val mPoweronRecRepository: IPoweronRecRepository
    val mRepairRecRepository: IRepairRecRepository
    val mSorftwareReplaceRecRepository: ISorftwareReplaceRecRepository
    val mTecReportImpRecRepository: ITecReportImpRecRepository
    val mCodeUpRecRepository: ICodeUpRecRepository
}

/**
 * [AppContainer] implementation that provides instance of [RecordRepositoryImpl]
 */
class AppDataContainer : AppContainer {
    override val mRecordRepository: IRecordRepository by lazy {
        RecordRepositoryImpl(AppDataDatabase.getDatabase().recordBeanDao())
    }
    override val mEquMatchRepository: IEquMatchRepository by lazy {
        EquMatchRepositoryImpl(AppDataDatabase.getDatabase().equMatchDao())
    }
    override val mEquReplaceRecRepository: IEquReplaceRecRepository by lazy {
        EquReplaceRecRepositoryImpl(AppDataDatabase.getDatabase().equReplaceRecDao())
    }
    override val mGasUpRecRepository: IGasUpRecRepository by lazy {
        GasUpRecRepositoryImpl(AppDataDatabase.getDatabase().gasUpRecDao())
    }
    override val mGJZBRecRepository: IGJZBRecRepository by lazy {
        GJZBRecRepositoryImpl(AppDataDatabase.getDatabase().gJZBRecDao())
    }
    override val mHandoverRecRepository: IHandoverRecRepository by lazy {
        HandoverRecRepositoryImpl(AppDataDatabase.getDatabase().handoverRecDao())
    }
    override val mImportantNoteRepository: ImportantNoteRepository by lazy {
        ImportantNoteRepositoryImpl(AppDataDatabase.getDatabase().importantNoteDao())
    }
    override val mMtRecRepository: IMtRecRepository by lazy {
        MtRecRepositoryImpl(AppDataDatabase.getDatabase().mtRecDao())
    }
    override val mPoweronRecRepository: IPoweronRecRepository by lazy {
        PoweronRecRepositoryImpl(AppDataDatabase.getDatabase().poweronRecDao())
    }
    override val mRepairRecRepository: IRepairRecRepository by lazy {
        RepairRecRepositoryImpl(AppDataDatabase.getDatabase().repairRecDao())
    }
    override val mSorftwareReplaceRecRepository: ISorftwareReplaceRecRepository by lazy {
        SorftwareReplaceRecRepositoryImpl(AppDataDatabase.getDatabase().sorftwareReplaceRecDao())
    }
    override val mTecReportImpRecRepository: ITecReportImpRecRepository by lazy {
        TecReportImpRecRepositoryImpl(AppDataDatabase.getDatabase().tecReportImpRecDao())
    }
    override val mCodeUpRecRepository: ICodeUpRecRepository by lazy {
        CodeUpRecRepositoryImpl(AppDataDatabase.getDatabase().codeUpRecDao())
    }


}