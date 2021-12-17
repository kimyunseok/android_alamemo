package com.landvibe.alamemo.repository

import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase

class MemoRepository {
    private val memoDataBase = AppDataBase.instance.memoDao()

    /*Memo*/
    suspend fun insertMemo(memo: Memo) = memoDataBase.suspendInsertMemo(memo)
    fun getMemoById(id: Long) = memoDataBase.getMemoById(id)
    fun getMemoByType(type: Int) = memoDataBase.getMemoByType(type)
    suspend fun deleteMemoByID(id: Long) = memoDataBase.suspendDeleteMemoByID(id)
    fun setMemoFinish(id: Long) = memoDataBase.setMemoFinish(id)
    suspend fun modifyMemo(id: Long, type: Int, icon: String,
                   title: String, scheduleDateYear: Int,
                   scheduleDateMonth: Int, scheduleDateDay: Int,
                   scheduleDateHour: Int, scheduleDateMinute: Int,
                   alarmStartTimeHour: Int, alarmStartTimeMinute: Int,
                   fixNotify: Boolean, setAlarm: Boolean, repeatDay: MutableList<Char>, alarmStartTimeType: Int)
            = memoDataBase.modifyMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute, alarmStartTimeHour, alarmStartTimeMinute, fixNotify, setAlarm, repeatDay, alarmStartTimeType)

}