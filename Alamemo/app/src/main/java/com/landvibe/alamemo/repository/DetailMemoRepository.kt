package com.landvibe.alamemo.repository

import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.database.AppDataBase

class DetailMemoRepository {
    private val detailMemoDataBase = AppDataBase.instance.detailMemoDao()

    /*DetailMemo*/
    fun getDetailMemoByMemoId(memoId: Long) = detailMemoDataBase.getDetailMemoByMemoId(memoId)
    fun getDetailMemoById(id: Long) = detailMemoDataBase.getDetailMemoById(id)
    suspend fun suspendInsertDetailMemo(detailMemo: DetailMemo) = detailMemoDataBase.suspendInsertDetailMemo(detailMemo)
    suspend fun deleteDetailMemo(detailMemo: DetailMemo) = detailMemoDataBase.deleteDetailMemo(detailMemo)
    suspend fun suspendDeleteDetailMemoByID(id: Long) = detailMemoDataBase.suspendDeleteDetailMemoByID(id)
    suspend fun deleteDetailMemoByMemoID(memoId: Long) = detailMemoDataBase.deleteDetailMemoByMemoID(memoId)
    suspend fun suspendModifyDetailMemo(id: Long, type: Int, icon: String,
                                        title: String, scheduleDateYear: Int,
                                        scheduleDateMonth: Int, scheduleDateDay: Int,
                                        scheduleDateHour: Int, scheduleDateMinute: Int
    ) = detailMemoDataBase.suspendModifyDetailMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute)

    fun modifyDetailMemoType(id: Long, type: Int, icon: String,
                             title: String, scheduleDateYear: Int,
                             scheduleDateMonth: Int, scheduleDateDay: Int,
                             scheduleDateHour: Int, scheduleDateMinute: Int) =
        detailMemoDataBase.modifyDetailMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute)

}