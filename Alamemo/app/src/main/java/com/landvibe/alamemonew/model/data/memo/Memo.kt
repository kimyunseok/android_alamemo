package com.landvibe.alamemonew.model.data.memo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Memo (
    @PrimaryKey(autoGenerate = true) var id: Long,
    var type: MutableLiveData<Int>, // 1 : 메모, 2 : 일정, 3 : 반복일정, 4 : 종료된 일정
    var icon: MutableLiveData<String>,
    var title: MutableLiveData<String>,
    var scheduleDate: MutableLiveData<Date>,
    var alarmStartTime: MutableLiveData<Date>,
    var fixNotify: MutableLiveData<Boolean>,
    var setAlarm: MutableLiveData<Boolean>,
    var repeatDay: MutableList<Char> // 반복일정에서 사용하는 반복 요일
) {
    val calendar = Calendar.getInstance()

    fun getDateFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale("ko", "KR"))
        return dateFormat.format(scheduleDate.value)
    }

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        scheduleDate.value?.let { curSetTime ->

            calendar.time = curSetTime
            calendar.set(year, month, day)

            scheduleDate.value = calendar.time
        }
    }

    fun setSceduleTime(hour: Int, minute: Int) {

    }

    fun setAlarmStartTime(hour: Int, minute: Int) {

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
}