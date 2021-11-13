package com.landvibe.alamemo.model.data.memo

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoDao {
    @Insert
    fun insertMemo(memo: Memo): Long // Long을 return하면 해당 memo의 id를 알 수 있다.

    @Delete
    fun deleteMemo(memo: Memo)

    @Query("SELECT * FROM Memo")
    fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo Where id = :id")
    fun getMemoById(id: Long): Memo

    @Query("SELECT * FROM Memo Where type = :type and scheduleFinish = :scheduleFinish")
    fun getMemoByType(type: Int, scheduleFinish: Boolean = false): List<Memo>

    @Query("SELECT * FROM Memo Where scheduleFinish = :scheduleFinish")
    fun getFinishMemo(scheduleFinish: Boolean = true): List<Memo>

    @Query("SELECT * FROM Memo Where setAlarm = :setAlarm")
    fun getAlarmMemo(setAlarm: Boolean = true): List<Memo>

    @Query("SELECT * FROM Memo Where fixNotify = :fixNotify")
    fun getFixNotifyMemo(fixNotify: Boolean = true): List<Memo>

    @Query("SELECT * FROM Memo Where type = :type")
    fun getRepeatScheduleMemo(type: Int = 3): List<Memo>

    @Query("DELETE FROM Memo Where id = :id")
    fun deleteMemoByID(id: Long)

    @Query("UPDATE Memo SET type = :type Where id = :id")
    fun modifyMemoType(id: Long, type: MutableLiveData<Int>)

    @Query("UPDATE Memo SET scheduleFinish = :scheduleFinish Where id = :id")
    fun setMemoFinish(id: Long, scheduleFinish: Boolean = true)

    @Query("UPDATE Memo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute, alarmStartTimeHour = :alarmStartTimeHour, alarmStartTimeMinute = :alarmStartTimeMinute, scheduleFinish = :scheduleFinish, fixNotify = :fixNotify, setAlarm = :setAlarm, repeatDay = :repeatDay, alarmStartTimeType = :alarmStartTimeType Where id = :id")
    fun modifyMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                   title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                   scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                   scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>,
                   alarmStartTimeHour: MutableLiveData<Int>, alarmStartTimeMinute: MutableLiveData<Int>, scheduleFinish: MutableLiveData<Boolean>,
                   fixNotify: MutableLiveData<Boolean>, setAlarm: MutableLiveData<Boolean>, repeatDay: MutableList<Char>, alarmStartTimeType: MutableLiveData<Int>)

    @Query("UPDATE Memo SET scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay Where id = :id")
    fun modifyMemoDate(id: Long, scheduleDateYear: MutableLiveData<Int>,
                   scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>)

}