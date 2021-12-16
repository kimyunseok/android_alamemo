package com.landvibe.alamemo.repository

import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.database.AppDataBase

class DetailMemoRepository {
    private val detailMemoDataBase = AppDataBase.instance.detailMemoDao()

    /*DetailMemo*/
    fun getDetailMemoByMemoId(memoId: Long) = detailMemoDataBase.getDetailMemoByMemoId(memoId)
    fun getDetailMemoById(id: Long) = detailMemoDataBase.getDetailMemoById(id)
    suspend fun insertDetailMemo(detailMemo: DetailMemo) = detailMemoDataBase.insertDetailMemo(detailMemo)
    suspend fun deleteDetailMemo(detailMemo: DetailMemo) = detailMemoDataBase.deleteDetailMemo(detailMemo)
    suspend fun deleteDetailMemoByID(id: Long) = detailMemoDataBase.deleteDetailMemoByID(id)
    suspend fun deleteDetailMemoByMemoID(memoId: Long) = detailMemoDataBase.deleteDetailMemoByMemoID(memoId)
    suspend fun modifyDetailMemo(id: Long, type: Int, icon: String,
                                 title: String, scheduleDateYear: Int,
                                 scheduleDateMonth: Int, scheduleDateDay: Int,
                                 scheduleDateHour: Int, scheduleDateMinute: Int
    ) = detailMemoDataBase.modifyDetailMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute)

}