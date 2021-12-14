package com.landvibe.alamemo.repository

import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.model.data.detail.DetailMemo

class DetailMemoRepository {
    private val detailMemoDataBase = AppDataBase.instance.detailMemoDao()

    /*DetailMemo*/
    suspend fun getDetailMemoByMemoId(memoId: Long) = detailMemoDataBase.getDetailMemoByMemoId(memoId)
    suspend fun getDetailMemoById(id: Long) = detailMemoDataBase.getDetailMemoById(id)
    suspend fun insertDetailMemo(detailMemo: DetailMemo) = detailMemoDataBase.insertDetailMemo(detailMemo)
    suspend fun deleteDetailMemo(detailMemo: DetailMemo) = detailMemoDataBase.deleteDetailMemo(detailMemo)
    suspend fun deleteDetailMemoByID(id: Long) = detailMemoDataBase.deleteDetailMemoByID(id)
    suspend fun deleteDetailMemoByMemoID(memoId: Long) = detailMemoDataBase.deleteDetailMemoByMemoID(memoId)
    suspend fun modifyDetailMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                                 title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                                 scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                                 scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>
    ) = detailMemoDataBase.modifyDetailMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute)

}