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
    private val _memoSaveComplete = MutableLiveData<Long>()
    val memoSaveComplete: LiveData<Long>
        get() = _memoSaveComplete

    //메모관련 - Binding 목적
    private val _memoId = MutableLiveData<Long>()
    val memoId: LiveData<Long>
        get() = _memoId
    val memoIdValue: Long
        get() = memoId.value?: -1L

    private val _memoType = MutableLiveData<Int>()
    val memoType: LiveData<Int>
        get() = _memoType
    val type: Int
        get() = memoType.value?: -1

    val memoIcon = MutableLiveData<String>()
    private val icon: String
        get() = memoIcon.value?: ""

    val memoTitle = MutableLiveData<String>()
    private val title: String
        get() = memoTitle.value?: ""

    val memoScheduleDateYear = MutableLiveData<Int>()
    val scheduleDateYear
        get() = memoScheduleDateYear.value?: Calendar.getInstance().get(Calendar.YEAR)


    val memoScheduleDateMonth = MutableLiveData<Int>()
    val scheduleDateMonth: Int
        get() = memoScheduleDateMonth.value?: Calendar.getInstance().get(Calendar.MONTH)

    val memoScheduleDateDay = MutableLiveData<Int>()
    val scheduleDateDay: Int
        get() = memoScheduleDateDay.value?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    val memoScheduleDateHour= MutableLiveData<Int>()
    val scheduleDateHour: Int
        get() = memoScheduleDateHour.value?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val memoScheduleDateMinute = MutableLiveData<Int>()
    val scheduleDateMinute: Int
        get() = memoScheduleDateMinute.value?: Calendar.getInstance().get(Calendar.MINUTE)

    val memoAlarmStartTimeHour = MutableLiveData<Int>()
    private val alarmStartTimeHour: Int
        get() = memoAlarmStartTimeHour.value ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val memoAlarmStartTimeMinute = MutableLiveData<Int>()
    private val alarmStartTimeMinute: Int
        get() = memoAlarmStartTimeMinute.value ?: Calendar.getInstance().get(Calendar.MINUTE)

    private val _memoFixNotify = MutableLiveData<Boolean>()
    val memoFixNotify: LiveData<Boolean>
        get() = _memoFixNotify
    val fixNotify: Boolean
        get() = memoFixNotify.value ?: false

    private val _memoSetAlarm = MutableLiveData<Boolean>()
    val memoSetAlarm: LiveData<Boolean>
        get() = _memoSetAlarm
    val setAlarm: Boolean
        get() = memoSetAlarm.value ?: false

    var memoRepeatDay = mutableListOf<Char>()
    var memoAlarmStartTimeType = 1

    private val _memoScheduleDateFormat = MutableLiveData<String>()
    val memoScheduleDateFormat: LiveData<String>
        get() = _memoScheduleDateFormat

    var prevType = -1

    fun initMemo() {
        val calendar = Calendar.getInstance()

        _memoId.postValue(0L)

        _memoType.postValue(1)
        memoIcon.postValue("📝")
        memoTitle.postValue("")

        //날짜는 바로 띄워줘야 하므로 setValue()로 처리
        memoScheduleDateYear.value = calendar.get(Calendar.YEAR)
        memoScheduleDateMonth.value = calendar.get(Calendar.MONTH)
        memoScheduleDateDay.value = calendar.get(Calendar.DAY_OF_MONTH)
        memoScheduleDateHour.value = calendar.get(Calendar.HOUR_OF_DAY)
        memoScheduleDateMinute.value = calendar.get(Calendar.MINUTE)
        memoAlarmStartTimeHour.value = calendar.get(Calendar.HOUR_OF_DAY)
        memoAlarmStartTimeMinute.value = calendar.get(Calendar.MINUTE)

        _memoFixNotify.postValue(false)
        _memoSetAlarm.postValue(false)

        setMemoScheduleDateFormatByDate()
    }

    fun getMemoInfoById(id: Long) {
        val memo = memoRepository.getMemoById(id)

        _memoId.postValue(id)

        // 이전 Type 저장해서 이전 Type도 newList로 Update.
        prevType = if(memo.scheduleFinish) {
            4
        } else {
            memo.type
        }

        _memoType.postValue(memo.type)
        memoIcon.postValue(memo.icon)
        memoTitle.postValue(memo.title)

        //날짜는 바로 띄워줘야 하므로 setValue()로 처리
        memoScheduleDateYear.value = memo.scheduleDateYear
        memoScheduleDateMonth.value = memo.scheduleDateMonth
        memoScheduleDateDay.value = memo.scheduleDateDay
        memoScheduleDateHour.value = memo.scheduleDateHour
        memoScheduleDateMinute.value = memo.scheduleDateMinute
        memoAlarmStartTimeHour.value = memo.alarmStartTimeHour
        memoAlarmStartTimeMinute.value = memo.alarmStartTimeMinute

        memoRepeatDay = memo.repeatDay
        memoAlarmStartTimeType = memo.alarmStartTimeType

        _memoFixNotify.postValue(memo.fixNotify)
        _memoSetAlarm.postValue(memo.setAlarm)

        setMemoScheduleDateFormatByDate()

        checkScheduleTime()
    }

    fun saveMemo() {
        if(title.trim() == "") {
            _memoSaveComplete.postValue(-1)
        } else if(type != 1 && type != 2 && type != 3) {
            _memoSaveComplete.postValue(-2)
        } else if(type == 3 && memoRepeatDay.isEmpty()) {
            _memoSaveComplete.postValue(-3)
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

            CoroutineScope(Dispatchers.IO).launch {
                if (memoIdValue != 0L) {
                    // 메모 id가 존재한다면, 즉 수정하기라면
                    memoRepository.modifyMemo(
                        id = memoIdValue,
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
                        fixNotify = fixNotify,
                        setAlarm = setAlarm,
                        repeatDay = memoRepeatDay,
                        alarmStartTimeType = memoAlarmStartTimeType,
                        scheduleFinish = false
                    )
                } else {
                    //메모 새로 생성하기라면 메모 삽입
                    val memoId = memoRepository.insertMemo(
                        Memo(
                            id = memoIdValue,
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
                            fixNotify = fixNotify,
                            setAlarm = setAlarm,
                            repeatDay = memoRepeatDay,
                            alarmStartTimeType = memoAlarmStartTimeType,
                            scheduleFinish = false
                        )
                    )

                    _memoId.postValue(memoId)
                }
                _memoSaveComplete.postValue(memoIdValue)
            }
        }
    }

    fun setType(type: Int) {
        _memoType.postValue(type)
    }

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        memoScheduleDateYear.value = year
        memoScheduleDateMonth.value = month
        memoScheduleDateDay.value = day
        setMemoScheduleDateFormatByDate()
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
        setMemoScheduleDateFormatByDate()
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

    private fun setMemoScheduleDateFormatByDate() {
        _memoScheduleDateFormat.postValue(MemoUtil().getScheduleDateFormat(scheduleDateYear, scheduleDateMonth, scheduleDateDay))
    }
}