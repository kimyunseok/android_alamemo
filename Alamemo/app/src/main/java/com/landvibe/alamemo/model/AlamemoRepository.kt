package com.landvibe.alamemo.model

import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo

class AlamemoRepository {
    private val memoDataBase = AppDataBase.instance.memoDao()
    private val detailMemoDataBase = AppDataBase.instance.detailMemoDao()

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
    suspend fun modifyMemoType(id: Long, type: MutableLiveData<Int>)  = memoDataBase.modifyMemoType(id, type)
    suspend fun setMemoFinish(id: Long, scheduleFinish: Boolean = true) = memoDataBase.setMemoFinish(id, scheduleFinish)
    suspend fun modifyMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                   title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                   scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                   scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>,
                   alarmStartTimeHour: MutableLiveData<Int>, alarmStartTimeMinute: MutableLiveData<Int>, scheduleFinish: MutableLiveData<Boolean>,
                   fixNotify: MutableLiveData<Boolean>, setAlarm: MutableLiveData<Boolean>, repeatDay: MutableList<Char>, alarmStartTimeType: MutableLiveData<Int>)
            = memoDataBase.modifyMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute, alarmStartTimeHour, alarmStartTimeMinute, scheduleFinish, fixNotify, setAlarm, repeatDay, alarmStartTimeType)
    suspend fun modifyMemoDate(id: Long, scheduleDateYear: MutableLiveData<Int>,
                       scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>)
            = memoDataBase.modifyMemoDate(id, scheduleDateYear, scheduleDateMonth, scheduleDateDay)

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