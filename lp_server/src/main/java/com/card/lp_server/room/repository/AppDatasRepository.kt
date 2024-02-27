package com.card.lp_server.room.repository

import com.card.lp_server.base.IBaseRepository
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

interface IRecordRepository : IBaseRepository<RecordBean> {

}

interface ICodeUpRecRepository : IBaseRepository<CodeUpRec> {

}

interface IEquMatchRepository : IBaseRepository<EquMatch> {

}

interface IEquReplaceRecRepository : IBaseRepository<EquReplaceRec> {

}

interface IGasUpRecRepository : IBaseRepository<GasUpRec> {

}

interface IGJZBRecRepository : IBaseRepository<GJZBRec> {

}

interface IHandoverRecRepository : IBaseRepository<HandoverRec> {

}

interface ImportantNoteRepository : IBaseRepository<ImportantNote> {

}

interface IMtRecRepository : IBaseRepository<MtRec> {

}

interface IPoweronRecRepository : IBaseRepository<PoweronRec> {

}

interface IRepairRecRepository : IBaseRepository<RepairRec> {

}

interface ISorftwareReplaceRecRepository : IBaseRepository<SorftwareReplaceRec> {

}

interface ITecReportImpRecRepository : IBaseRepository<TecReportImpRec> {

}