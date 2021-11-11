package com.landvibe.alamemonew.model.data.memo

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo Where id = :id")
    fun getMemoById(id: Long): Memo

    @Query("SELECT * FROM Memo Where type = :type")
    fun getMemoByType(type: Int): List<Memo>

    @Insert
    fun insertMemo(memoViewModel: Memo)

    @Delete
    fun deleteMemo(memoViewModel: Memo)

    @Query("UPDATE Memo SET type = :type Where id = :id")
    fun modifyMemoType(id: Long, type: MutableLiveData<Int>)

    @Query("UPDATE Memo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute, alarmStartTimeHour = :alarmStartTimeHour, alarmStartTimeMinute = :alarmStartTimeMinute, fixNotify = :fixNotify, setAlarm = :setAlarm, repeatDay = :repeatDay, alarmStartTimeType = :alarmStartTimeType Where id = :id")
    fun modifyMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                   title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                   scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                   scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>,
                   alarmStartTimeHour: MutableLiveData<Int>, alarmStartTimeMinute: MutableLiveData<Int>,
                   fixNotify: MutableLiveData<Boolean>, setAlarm: MutableLiveData<Boolean>, repeatDay: MutableList<Char>, alarmStartTimeType: MutableLiveData<Int>)
}