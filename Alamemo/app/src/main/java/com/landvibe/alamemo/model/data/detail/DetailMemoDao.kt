package com.landvibe.alamemo.model.data.detail

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DetailMemoDao {
    @Query("SELECT * FROM DetailMemo Where memoId = :memoId ")
    fun getDetailMemoByMemoId(memoId: Long): List<DetailMemo>

    @Query("SELECT * FROM DetailMemo Where id = :id")
    fun getDetailMemoById(id: Long): DetailMemo

    @Insert
    fun insertDetailMemo(detailMemo: DetailMemo)

    @Delete
    fun deleteDetailMemo(detailMemo: DetailMemo)

    @Query("DELETE FROM DetailMemo Where id = :id")
    fun deleteDetailMemoByID(id: Long)

    @Query("DELETE FROM DetailMemo Where memoId = :memoId")
    fun deleteDetailMemoByMemoID(memoId: Long)

    @Query("UPDATE DetailMemo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute Where id = :id")
    fun modifyDetailMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                   title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                   scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                   scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>
    )
}