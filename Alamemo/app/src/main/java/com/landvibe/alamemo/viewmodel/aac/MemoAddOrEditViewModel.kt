package com.landvibe.alamemo.viewmodel.aac

import android.util.Log
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
    private val _memoSaveComplete = MutableLiveData<Boolean>()
    val memoSaveComplete: LiveData<Boolean>
        get() = _memoSaveComplete

    //메모관련 - Binding 목적
    private val _memoId = MutableLiveData<Long>()
    val memoId: LiveData<Long>
        get() = _memoId
    val memoIdValue = memoId.value as Long

    private val _memoType = MutableLiveData<Int>()
    val memoType: LiveData<Int>
        get() = _memoType
    private val type = memoType.value as Int

    val memoIcon = MutableLiveData<String>()
    private val icon = memoIcon.value as String

    val memoTitle = MutableLiveData<String>()
    private val title = memoTitle.value as String

    val memoScheduleDateYear = MutableLiveData<Int>()
    val scheduleDateYear = memoScheduleDateYear.value as Int

    val memoScheduleDateMonth = MutableLiveData<Int>()
    val scheduleDateMonth = memoScheduleDateMonth.value as Int

    val memoScheduleDateDay = MutableLiveData<Int>()
    val scheduleDateDay = memoScheduleDateDay.value as Int

    val memoScheduleDateHour= MutableLiveData<Int>()
    val scheduleDateHour = memoScheduleDateHour.value as Int

    val memoScheduleDateMinute = MutableLiveData<Int>()
    val scheduleDateMinute = memoScheduleDateMinute.value as Int

    val memoAlarmStartTimeHour = MutableLiveData<Int>()
    private val alarmStartTimeHour = memoAlarmStartTimeHour.value as Int

    val memoAlarmStartTimeMinute = MutableLiveData<Int>()
    private val alarmStartTimeMinute = memoAlarmStartTimeMinute.value as Int

    private val _memoFixNotify = MutableLiveData<Boolean>()
    val memoFixNotify: LiveData<Boolean>
        get() = _memoFixNotify
    val fixNotify = memoFixNotify.value as Boolean

    private val _memoSetAlarm = MutableLiveData<Boolean>()
    val memoSetAlarm: LiveData<Boolean>
        get() = _memoSetAlarm
    val setAlarm = memoSetAlarm.value as Boolean

    var memoRepeatDay = mutableListOf<Char>()
    var memoAlarmStartTimeType = 1

    private val _memoScheduleDateFormat = MutableLiveData<String>()
    val memoScheduleDateFormat: LiveData<String>
        get() = _memoScheduleDateFormat


    fun initMemo() {
        val calendar = Calendar.getInstance()

        _memoId.postValue(0L)

        _memoType.postValue(1)
        memoIcon.postValue("📝")
        memoTitle.postValue("")
        memoScheduleDateYear.postValue(calendar.get(Calendar.YEAR))
        memoScheduleDateMonth.postValue(calendar.get(Calendar.MONTH))
        memoScheduleDateDay.postValue(calendar.get(Calendar.DAY_OF_MONTH))
        memoScheduleDateHour.postValue(calendar.get(Calendar.HOUR_OF_DAY))
        memoScheduleDateMinute.postValue(calendar.get(Calendar.MINUTE))
        memoAlarmStartTimeHour.postValue(calendar.get(Calendar.HOUR_OF_DAY))
        memoAlarmStartTimeMinute.postValue(calendar.get(Calendar.MINUTE))
        _memoFixNotify.postValue(false)
        _memoSetAlarm.postValue(false)

        val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeekByDate(scheduleDateYear, scheduleDateMonth, scheduleDateDay)
        _memoScheduleDateFormat.postValue("${(calendar.get(Calendar.YEAR))}년 ${(calendar.get(Calendar.MONTH)).plus(1)}월 " +
                "${(calendar.get(Calendar.DAY_OF_MONTH))}일 ${dayOfWeek}요일")
    }

    fun getMemoInfoById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val memo = memoRepository.getMemoById(id)

            _memoId.postValue(id)

            _memoType.postValue(memo.type)
            memoIcon.postValue(memo.icon)
            memoTitle.postValue(memo.title)
            memoScheduleDateYear.postValue(memo.scheduleDateYear)
            memoScheduleDateMonth.postValue(memo.scheduleDateMonth)
            memoScheduleDateDay.postValue(memo.scheduleDateDay)
            memoScheduleDateHour.postValue(memo.scheduleDateHour)
            memoScheduleDateMinute.postValue(memo.scheduleDateMinute)
            memoAlarmStartTimeHour.postValue(memo.alarmStartTimeHour)
            memoAlarmStartTimeMinute.postValue(memo.alarmStartTimeMinute)
            memoRepeatDay = memo.repeatDay
            memoAlarmStartTimeType = memo.alarmStartTimeType
            _memoFixNotify.postValue(memo.fixNotify)
            _memoSetAlarm.postValue(memo.setAlarm)

            val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeekByDate(scheduleDateYear, scheduleDateMonth, scheduleDateDay)

            _memoScheduleDateFormat.postValue("${memo.scheduleDateYear}년 ${(memo.scheduleDateMonth).plus(1)}월 " +
                    "${memo.scheduleDateDay}일 ${dayOfWeek}요일")

            checkScheduleTime()
        }
    }

    fun saveMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            if(title.trim() == "") {
                _memoSaveComplete.postValue(false)
            } else {
                Log.d("MemoAddOrEdit", "Memo Save To Room")
                if(type == 1) {
                    //메모라면 날짜를 오늘로 수정.
                    setMemoScheduleTimeToday()
                    _memoSetAlarm.postValue(false) // 메모라면 알람기능 해제
                }

                if(type != 3) {
                    //반복 일정 아니라면, 반복 요일 선택했던 거 다 지움.
                    memoRepeatDay.clear()
                } else {
                    //반복 일정이라면 반복 요일 중 최초로 해당하는 날로 scheduleDay 설정.
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = AboutDay.AboutDayOfWeek()
                        .findMinTimeAboutDayOfWeekBySpecificTime(memoRepeatDay,
                            alarmStartTimeHour,
                            alarmStartTimeMinute,
                            System.currentTimeMillis()
                        )

                    setScheduleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                }

                if (memoIdValue != 0L) {
                    // 메모 id가 존재한다면, 즉 수정하기라면
                    // 메모 수정.

                    memoRepository.modifyMemo(
                        memoIdValue,
                        type = type,
                        icon = icon,
                        title = title,
                        scheduleDateYear = scheduleDateYear,
                        scheduleDateMonth = scheduleDateMonth,
                        scheduleDateDay = scheduleDateDay,
                        scheduleDateHour = scheduleDateHour,
                        scheduleDateMinute = scheduleDateMinute,
                        alarmStartTimeHour = alarmStartTimeHour,
                        alarmStartTimeMinute = alarmStartTimeMinute,
                        false,
                        fixNotify = fixNotify,
                        setAlarm = setAlarm,
                        repeatDay = memoRepeatDay,
                        alarmStartTimeType = memoAlarmStartTimeType
                    )

                } else {
                    //메모 새로 생성하기라면 메모 삽입

                    memoRepository.insertMemo(
                        Memo(
                            0,
                            type = type,
                            icon = icon,
                            title = title,
                            scheduleDateYear = scheduleDateYear,
                            scheduleDateMonth = scheduleDateMonth,
                            scheduleDateDay = scheduleDateDay,
                            scheduleDateHour = scheduleDateHour,
                            scheduleDateMinute = scheduleDateMinute,
                            alarmStartTimeHour = alarmStartTimeHour,
                            alarmStartTimeMinute = alarmStartTimeMinute,
                            false,
                            fixNotify = fixNotify,
                            setAlarm = setAlarm,
                            repeatDay = memoRepeatDay,
                            alarmStartTimeType = memoAlarmStartTimeType
                        )
                    )

                }
                _memoSaveComplete.postValue(true)
            }
        }
    }

    fun setType(type: Int) {
        _memoType.postValue(type)
    }

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        memoScheduleDateYear.postValue(year)
        memoScheduleDateMonth.postValue(month)
        memoScheduleDateMonth.postValue(day)
        resetMemoScheduleDateFormatByDate()
    }

    fun setSceduleTime(hour: Int, minute: Int) {
        memoScheduleDateHour.postValue(hour)
        memoScheduleDateMinute.postValue(minute)
    }

    fun setAlarmStartTime(hour: Int, minute: Int) {
        memoAlarmStartTimeHour.postValue(hour)
        memoAlarmStartTimeMinute.postValue(minute)
    }

    fun setRepeatDay(dayOfWeek: Char) {
        if (memoRepeatDay.contains(dayOfWeek)) {
            memoRepeatDay.remove(dayOfWeek)
        } else {
            memoRepeatDay.add(dayOfWeek)
        }
    }

    fun setNotify(notify: Boolean) {
        _memoFixNotify.postValue(notify)
    }

    fun setAlarm(alarm: Boolean) {
        _memoSetAlarm.postValue(alarm)
    }

    fun setAlarmStartType(type: Int) {
        memoAlarmStartTimeType = type
    }

    private fun setMemoScheduleTimeToday() {
        val calendar = Calendar.getInstance()
        setScheduleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        setSceduleTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        resetMemoScheduleDateFormatByDate()
    }

    private fun checkScheduleTime() {
        val todayCalendar = Calendar.getInstance()
        val checkCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, scheduleDateYear)
            set(Calendar.MONTH, scheduleDateMonth)
            set(Calendar.DAY_OF_MONTH, scheduleDateDay)
            set(Calendar.HOUR_OF_DAY, scheduleDateHour)
            set(Calendar.MINUTE, scheduleDateMinute)
        }

        //메모에 설정된 시간이 이전 시간이라면 오늘 시간으로 변경한다.
        if(checkCalendar.timeInMillis < todayCalendar.timeInMillis) {
            setMemoScheduleTimeToday()
        }
    }

    private fun resetMemoScheduleDateFormatByDate() {
        if (type != 3) {
            _memoScheduleDateFormat.postValue(MemoUtil().getScheduleDateFormat(scheduleDateYear, scheduleDateMonth, scheduleDateDay))
        } else {
            _memoScheduleDateFormat.postValue(MemoUtil().getRepeatScheduleDateFormat(memoRepeatDay))
        }
    }
}