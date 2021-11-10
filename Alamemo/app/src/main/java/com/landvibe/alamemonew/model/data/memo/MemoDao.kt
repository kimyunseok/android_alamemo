package com.landvibe.alamemonew.model.data.memo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.landvibe.alamemonew.model.uimodel.MemoViewModel
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

    @Query("UPDATE Memo SET type = :type, icon = :icon, title = :title, scheduleDate = :scheduleDate, alarmStartTime = :alarmStartTime, fixNotify = :fixNotify, setAlarm = :setAlarm, repeatDay = :repeatDay, alarmStartTimeType = :alarmStartTimeType Where id = :id")
    fun modifyMemo(id: Long, type: Int, icon: String, title: String, scheduleDate: Date, alarmStartTime: Date, fixNotify: Boolean, setAlarm: Boolean, repeatDay: String, alarmStartTimeType: Int)
}