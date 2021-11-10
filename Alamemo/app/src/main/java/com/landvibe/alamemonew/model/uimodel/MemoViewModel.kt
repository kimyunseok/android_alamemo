package com.landvibe.alamemonew.model.uimodel

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

data class MemoViewModel (
    var id: Long,
    var type: MutableLiveData<Int>, // 1 : 메모, 2 : 일정, 3 : 반복일정, 4 : 종료된 일정
    var icon: MutableLiveData<String>,
    var title: MutableLiveData<String>,
    var scheduleDate: MutableLiveData<Date>,
    var alarmStartTime: MutableLiveData<Date>,
    var fixNotify: MutableLiveData<Boolean>,
    var setAlarm: MutableLiveData<Boolean>,
    var alarmStartTimeType: MutableLiveData<Int>, // 1 : 매일, 2 : 1주일 전, 3 : 3일 전, 4 : 하루 전
    var repeatDay: MutableList<Char> // 반복일정에서 사용하는 반복 요일
) {
    val calendar = Calendar.getInstance()

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        scheduleDate.value?.let { curSetTime ->

            calendar.time = curSetTime
            calendar.set(year, month, day)

            scheduleDate.value = calendar.time
        }
    }

    fun setSceduleTime(hour: Int, minute: Int) {
        scheduleDate.value?.let { curSetTime ->

            calendar.time = curSetTime
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, minute)

            scheduleDate.value = calendar.time
        }
    }

    fun setAlarmStartTime(hour: Int, minute: Int) {
        alarmStartTime.value?.let { curSetTime ->

            calendar.time = curSetTime
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, minute)

            alarmStartTime.value = calendar.time
        }
    }

    fun setType(type: Int) {
        this.type.value = type
    }

    fun setRepeatDay(dayOfWeek: Char) {
        if(repeatDay.contains(dayOfWeek)) {
            repeatDay.remove(dayOfWeek)
        } else {
            repeatDay.add(dayOfWeek)
        }
    }

    fun setNotify(notify: Boolean) {
        fixNotify.value = notify
    }

    fun setAlarm(alarm: Boolean) {
        setAlarm.value = alarm
    }

    fun setAlarmStartType(type: Int) {
        alarmStartTimeType.value = type
    }

    fun getDateFormat(): String {
        val setTime = scheduleDate.value?.time
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale("ko", "KR"))
        return dateFormat.format(setTime)
    }

    fun getDDay(): String {
        val today = ( System.currentTimeMillis() + (60 * 60 * 9 * 1000) ) / (60 * 60 * 24 * 1000)
        val setDateDay = ( scheduleDate.value?.time?.plus((60 * 60 * 9 * 1000) )?.div((60 * 60 * 24 * 1000)))

        if(setDateDay != null) {
            return when {
                setDateDay - today > 0 -> {
                    "D - ${(setDateDay - today)}"
                }
                setDateDay - today < 0 -> {
                    "D + ${(setDateDay - today)}"
                }
                else -> {
                    "D - DAY"
                }
            }
        }

        return ""
    }
}