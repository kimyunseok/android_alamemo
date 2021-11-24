package com.landvibe.alamemo.model.data.memo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.AlamemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel (val repository: AlamemoRepository): ViewModel() {
    //아래 Repositories들을 Observe한다.
    val insertMemoRepositories = MutableLiveData<Long>()
    val deleteMemoRepositories = MutableLiveData<Long>()
    val getAllMemoRepositories = MutableLiveData<List<Memo>>()
    val getMemoByIdRepositories = MutableLiveData<Memo>()
    val getMemoByTypeRepositories = MutableLiveData<List<Memo>>()
    val getFinishMemoRepositories = MutableLiveData<List<Memo>>()
    val getAlarmMemoRepositories = MutableLiveData<List<Memo>>()
    val getFixNotifyMemoRepositories = MutableLiveData<List<Memo>>()
    val getRepeatScheduleMemoRepositories = MutableLiveData<List<Memo>>()
    val deleteMemoByIDRepositories = MutableLiveData<Long>()
    val modifyMemoTypeRepositories = MutableLiveData<Long>()
    val setMemoFinishRepositories = MutableLiveData<Long>()
    val modifyMemoRepositories = MutableLiveData<Long>()
    val modifyMemoDateRepositories = MutableLiveData<Long>()

    fun insertMemo(memo: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertMemo(memo).let {
                id -> insertMemoRepositories.postValue(id)
            }
        }
    }

    fun deleteMemo(memo: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteMemo(memo).let {
                deleteMemoRepositories.postValue(System.currentTimeMillis())
            }
        }
    }

     fun getAllMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAllMemo().let {
                memoList -> getAllMemoRepositories.postValue(memoList)
            }
        }
    }

     fun getMemoById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getMemoById(id).let {
                memo -> getMemoByIdRepositories.postValue(memo)
            }
        }
    }

     fun getMemoByType(type: Int, scheduleFinish: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getMemoByType(type, scheduleFinish).let {
                memoList -> getMemoByTypeRepositories.postValue(memoList)
            }
        }
    }

     fun getFinishMemo(scheduleFinish: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getFinishMemo(scheduleFinish).let {
                memoList -> getFinishMemoRepositories.postValue(memoList)
            }
        }
    }

     fun getAlarmMemo(setAlarm: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAlarmMemo().let {
                memoList -> getAlarmMemoRepositories.postValue(memoList)
            }
        }
    }

     fun getFixNotifyMemo(fixNotify: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getFixNotifyMemo().let {
                memoList -> getFixNotifyMemoRepositories.postValue(memoList)
            }
        }
    }

     fun getRepeatScheduleMemo(type: Int = 3) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getRepeatScheduleMemo().let {
                memoList -> getRepeatScheduleMemoRepositories.postValue(memoList)
            }
        }
    }

     fun deleteMemoByID(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteMemoByID(id).let {
                deleteMemoByIDRepositories.postValue(System.currentTimeMillis())
            }
        }
    }

     fun modifyMemoType(id: Long, type: MutableLiveData<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.modifyMemoType(id, type).let {
                modifyMemoTypeRepositories.postValue(System.currentTimeMillis())
            }
        }
    }

     fun setMemoFinish(id: Long, scheduleFinish: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.setMemoFinish(id, scheduleFinish).let {
                setMemoFinishRepositories.postValue(System.currentTimeMillis())
            }
        }
    }

     fun modifyMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                           title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                           scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                           scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>,
                           alarmStartTimeHour: MutableLiveData<Int>, alarmStartTimeMinute: MutableLiveData<Int>, scheduleFinish: MutableLiveData<Boolean>,
                           fixNotify: MutableLiveData<Boolean>, setAlarm: MutableLiveData<Boolean>, repeatDay: MutableList<Char>, alarmStartTimeType: MutableLiveData<Int>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.modifyMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute, alarmStartTimeHour, alarmStartTimeMinute, scheduleFinish, fixNotify, setAlarm, repeatDay, alarmStartTimeType)
                .let {
                    modifyMemoRepositories.postValue(System.currentTimeMillis())
                }
        }
    }

     fun modifyMemoDate(id: Long, scheduleDateYear: MutableLiveData<Int>,
                               scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.modifyMemoDate(id, scheduleDateYear, scheduleDateMonth, scheduleDateDay)
                .let {
                    modifyMemoDateRepositories.postValue(System.currentTimeMillis())
                }
        }
    }
}