package com.landvibe.alamemo.repository

import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase

class MemoRepository {
    private val memoDataBase = AppDataBase.instance.memoDao()

    /*Memo*/
    suspend fun insertMemo(memo: Memo) = memoDataBase.insertMemo(memo)
    suspend fun deleteMemo(memo: Memo) = memoDataBase.deleteMemo(memo)
    suspend fun getAllMemo() = memoDataBase.getAllMemo()
    suspend fun getMemoById(id: Long) = memoDataBase.getMemoById(id)
    suspend fun getMemoByType(type: Int, scheduleFinish: Boolean = false) = memoDataBase.getMemoByType(type, scheduleFinish)
    suspend fun getFinishMemo(scheduleFinish: Boolean = true) = memoDataBase.getFinishMemo()
    suspend fun getAlarmMemo(setAlarm: Boolean = true) = memoDataBase.getAlarmMemo()
    suspend fun getFixNotifyMemo(fixNotify: Boolean = true) = memoDataBase.getFixNotifyMemo()
    suspend fun getRepeatScheduleMemo(type: Int = 3) = memoDataBase.getRepeatScheduleMemo()
    suspend fun deleteMemoByID(id: Long) = memoDataBase.deleteMemoByID(id)
    suspend fun modifyMemoType(id: Long, type: Int)  = memoDataBase.modifyMemoType(id, type)
    suspend fun setMemoFinish(id: Long, scheduleFinish: Boolean = true) = memoDataBase.setMemoFinish(id, scheduleFinish)
    suspend fun modifyMemo(id: Long, type: Int, icon: String,
                   title: String, scheduleDateYear: Int,
                   scheduleDateMonth: Int, scheduleDateDay: Int,
                   scheduleDateHour: Int, scheduleDateMinute: Int,
                   alarmStartTimeHour: Int, alarmStartTimeMinute: Int, scheduleFinish: Boolean,
                   fixNotify: Boolean, setAlarm: Boolean, repeatDay: MutableList<Char>, alarmStartTimeType: Int)
            = memoDataBase.modifyMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute, alarmStartTimeHour, alarmStartTimeMinute, scheduleFinish, fixNotify, setAlarm, repeatDay, alarmStartTimeType)
    suspend fun modifyMemoDate(id: Long, scheduleDateYear: Int,
                       scheduleDateMonth: Int, scheduleDateDay: Int)
            = memoDataBase.modifyMemoDate(id, scheduleDateYear, scheduleDateMonth, scheduleDateDay)

}