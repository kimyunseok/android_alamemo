package com.landvibe.alamemonew.model.data.memo

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo Where id = :id")
    fun getMemoById(id: Long): Memo

    @Query("SELECT * FROM Memo Where type = :type and scheduleFinish = ${false}")
    fun getMemoByType(type: Int): List<Memo>

    @Query("SELECT * FROM Memo Where scheduleFinish = ${true}")
    fun getFinishMemo(): List<Memo>

    @Insert
    fun insertMemo(memo: Memo)

    @Delete
    fun deleteMemo(memo: Memo)

    @Query("DELETE FROM Memo Where id = :id")
    fun deleteMemoByID(id: Long)

    @Query("UPDATE Memo SET type = :type Where id = :id")
    fun modifyMemoType(id: Long, type: MutableLiveData<Int>)

    @Query("UPDATE Memo SET scheduleFinish = ${true} Where id = :id")
    fun setMemoFinish(id: Long)

    @Query("UPDATE Memo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute, alarmStartTimeHour = :alarmStartTimeHour, alarmStartTimeMinute = :alarmStartTimeMinute, scheduleFinish = :scheduleFinish, fixNotify = :fixNotify, setAlarm = :setAlarm, repeatDay = :repeatDay, alarmStartTimeType = :alarmStartTimeType Where id = :id")
    fun modifyMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                   title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                   scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                   scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>,
                   alarmStartTimeHour: MutableLiveData<Int>, alarmStartTimeMinute: MutableLiveData<Int>, scheduleFinish: MutableLiveData<Boolean>,
                   fixNotify: MutableLiveData<Boolean>, setAlarm: MutableLiveData<Boolean>, repeatDay: MutableList<Char>, alarmStartTimeType: MutableLiveData<Int>)
}