package com.landvibe.alamemo.viewmodel.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.util.AboutDay
import com.landvibe.alamemo.util.MemoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MemoAddOrEditViewModel(private val memoRepository: MemoRepository): ViewModel() {
    private val _memoInfo = MutableLiveData<Memo>()
    val memoInfo: LiveData<Memo>
        get() = _memoInfo
    val currentMemo: Memo?
        get() = memoInfo.value

    private val _memoSaveComplete = MutableLiveData<Boolean>()
    val memoSaveComplete: LiveData<Boolean>
        get() = _memoSaveComplete

    fun initMemo() {
        val calendar = Calendar.getInstance()

        val memo = Memo(id = 0,
            type = (1),
            icon = ("📝"),
            title = (""),
            scheduleDateYear = (calendar.get(Calendar.YEAR)),
            scheduleDateMonth = (calendar.get(Calendar.MONTH)),
            scheduleDateDay = (calendar.get(Calendar.DAY_OF_MONTH)),
            scheduleDateHour = (calendar.get(Calendar.HOUR_OF_DAY)),
            scheduleDateMinute = (calendar.get(Calendar.MINUTE)),
            alarmStartTimeHour = (calendar.get(Calendar.HOUR_OF_DAY)),
            alarmStartTimeMinute = (calendar.get(Calendar.MINUTE)),
            scheduleFinish = (false),
            fixNotify = (false),
            setAlarm = (false),
            repeatDay = mutableListOf(),
            alarmStartTimeType = (1),
            showDateFormat = "${(calendar.get(Calendar.YEAR))}년 ${(calendar.get(Calendar.MONTH)).plus(1)}월 " +
                    "${(calendar.get(Calendar.DAY_OF_MONTH))}일 ${calendar.get(Calendar.DAY_OF_WEEK)}요일"
        )
        _memoInfo.postValue(memo)
    }

    fun getMemoInfoById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val memo = memoRepository.getMemoById(id)
            _memoInfo.postValue(memo)
            checkScheduleTime()
        }
    }

    fun saveMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            if(currentMemo?.title?.trim() == "") {
                _memoSaveComplete.postValue(false)
            } else {
                if(currentMemo?.type == 1) {
                    //메모라면 날짜를 오늘로 수정.
                    setMemoScheduleTimeToday()
                    currentMemo?.setAlarm = false // 메모라면 알람기능 해제
                }

                if(currentMemo?.type != 3) {
                    //반복 일정 아니라면, 반복 요일 선택했던 거 다 지움.
                    currentMemo?.repeatDay?.clear()
                } else {
                    //반복 일정이라면 반복 요일 중 최초로 해당하는 날로 scheduleDay 설정.
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = AboutDay.AboutDayOfWeek().findMinTimeAboutDayOfWeekBySpecificTime(currentMemo, System.currentTimeMillis())
                    setScheduleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                }

                if (currentMemo?.id != 0L) {
                    // 메모 id가 존재한다면, 즉 수정하기라면
                    // 메모 수정.

                    currentMemo?.let {
                        memoRepository.modifyMemo(
                            it.id,
                            type = it.type,
                            icon = it.icon,
                            title = it.title,
                            scheduleDateYear = it.scheduleDateYear,
                            scheduleDateMonth = it.scheduleDateMonth,
                            scheduleDateDay = it.scheduleDateDay,
                            scheduleDateHour = it.scheduleDateHour,
                            scheduleDateMinute = it.scheduleDateMinute,
                            alarmStartTimeHour = it.alarmStartTimeHour,
                            alarmStartTimeMinute = it.alarmStartTimeMinute,
                            false,
                            fixNotify = it.fixNotify,
                            setAlarm = it.setAlarm,
                            repeatDay = it.repeatDay,
                            alarmStartTimeType = it.alarmStartTimeType
                        )
                        _memoSaveComplete.postValue(true)
                    }

                } else {
                    //메모 새로 생성하기라면

                    // 메모 삽입.
                    currentMemo?.let {
                        memoRepository.insertMemo(it)
                        _memoSaveComplete.postValue(true)
                    }
                }

            }
        }
    }

    fun setType(type: Int) {
        currentMemo?.type = type
    }


    fun setScheduleDate(year: Int, month: Int, day: Int) {
        currentMemo?.scheduleDateYear = year
        currentMemo?.scheduleDateMonth = month
        currentMemo?.scheduleDateMonth = day
        MemoUtil().setMemoDate(currentMemo)
    }

    fun setSceduleTime(hour: Int, minute: Int) {
        currentMemo?.scheduleDateHour = hour
        currentMemo?.scheduleDateMinute = minute
        MemoUtil().setMemoDate(currentMemo)
    }

    fun setAlarmStartTime(hour: Int, minute: Int) {
        currentMemo?.alarmStartTimeHour = hour
        currentMemo?.alarmStartTimeMinute = minute
    }

    fun setRepeatDay(dayOfWeek: Char) {
        if (currentMemo?.repeatDay?.contains(dayOfWeek) == true) {
            currentMemo?.repeatDay?.remove(dayOfWeek)
        } else {
            currentMemo?.repeatDay?.add(dayOfWeek)
        }
    }

    fun setNotify(notify: Boolean) {
        currentMemo?.fixNotify = notify
    }

    fun setAlarm(alarm: Boolean) {
        currentMemo?.setAlarm = alarm
    }

    fun setAlarmStartType(type: Int) {
        currentMemo?.alarmStartTimeType = type
    }

    private fun setMemoScheduleTimeToday() {
        val calendar = Calendar.getInstance()
        setScheduleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        setSceduleTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        MemoUtil().setMemoDate(currentMemo)
    }

    private fun checkScheduleTime() {
        val todayCalendar = Calendar.getInstance()
        val checkCalendar = Calendar.getInstance().apply {
            currentMemo?.scheduleDateYear?.let { set(Calendar.YEAR, it) }
            currentMemo?.scheduleDateMonth?.let { set(Calendar.MONTH, it) }
            currentMemo?.scheduleDateDay?.let { set(Calendar.DAY_OF_MONTH, it) }
            currentMemo?.scheduleDateHour?.let { set(Calendar.HOUR_OF_DAY, it) }
            currentMemo?.scheduleDateMinute?.let { set(Calendar.MINUTE, it) }
        }

        //메모에 설정된 시간이 이전 시간이라면 오늘 시간으로 변경한다.
        if(checkCalendar.timeInMillis < todayCalendar.timeInMillis) {
            setMemoScheduleTimeToday()
        }
    }
}