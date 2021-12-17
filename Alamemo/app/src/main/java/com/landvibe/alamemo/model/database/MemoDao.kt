package com.landvibe.alamemo.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.landvibe.alamemo.model.data.memo.Memo

@Dao
interface MemoDao {
    @Insert
    suspend fun suspendInsertMemo(memo: Memo): Long // Long을 return하면 해당 memo의 id를 알 수 있다.
    @Insert
    fun insertMemo(memo: Memo): Long // Long을 return하면 해당 memo의 id를 알 수 있다.

    @Delete
    suspend fun deleteMemo(memo: Memo)

    @Query("SELECT * FROM Memo")
    suspend fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo Where id = :id")
    fun getMemoById(id: Long): Memo

    @Query("SELECT * FROM Memo Where type = :type")
    fun getMemoByType(type: Int): List<Memo>

    @Query("SELECT * FROM Memo Where setAlarm = :setAlarm")
    suspend fun getAlarmMemo(setAlarm: Boolean = true): List<Memo>

    @Query("SELECT * FROM Memo Where fixNotify = :fixNotify")
    suspend fun getFixNotifyMemo(fixNotify: Boolean = true): List<Memo>

    @Query("SELECT * FROM Memo Where type = :type")
    suspend fun getRepeatScheduleMemo(type: Int = 3): List<Memo>

    @Query("DELETE FROM Memo Where id = :id")
    suspend fun suspendDeleteMemoByID(id: Long)

    @Query("DELETE FROM Memo Where id = :id")
    fun deleteMemoByID(id: Long)

    @Query("UPDATE Memo SET type = :type Where id = :id")
    suspend fun modifyMemoType(id: Long, type: Int)

    @Query("UPDATE Memo SET type = :type Where id = :id")
    fun setMemoFinish(id: Long, type: Int = 4)

    @Query("UPDATE Memo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute, alarmStartTimeHour = :alarmStartTimeHour, alarmStartTimeMinute = :alarmStartTimeMinute, fixNotify = :fixNotify, setAlarm = :setAlarm, repeatDay = :repeatDay, alarmStartTimeType = :alarmStartTimeType Where id = :id")
    suspend fun modifyMemo(id: Long, type: Int, icon: String,
                   title: String, scheduleDateYear: Int,
                   scheduleDateMonth: Int, scheduleDateDay: Int,
                   scheduleDateHour: Int, scheduleDateMinute: Int,
                   alarmStartTimeHour: Int, alarmStartTimeMinute: Int,
                   fixNotify: Boolean, setAlarm: Boolean, repeatDay: MutableList<Char>, alarmStartTimeType: Int)

    @Query("UPDATE Memo SET scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay Where id = :id")
    suspend fun modifyMemoDate(id: Long, scheduleDateYear: Int,
                   scheduleDateMonth: Int, scheduleDateDay: Int)

}