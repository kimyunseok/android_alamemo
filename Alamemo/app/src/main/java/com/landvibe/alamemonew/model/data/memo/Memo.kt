package com.landvibe.alamemonew.model.data.memo

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.landvibe.alamemonew.util.AboutDay
import java.util.*

@Entity
class Memo (
    @PrimaryKey(autoGenerate = true) var id: Long,
    var type: MutableLiveData<Int>, // 1 : 메모, 2 : 일정, 3 : 반복일정, 4 : 종료된 일정
    var icon: MutableLiveData<String>,
    var title: MutableLiveData<String>,
    var scheduleDateYear: MutableLiveData<Int>,
    var scheduleDateMonth: MutableLiveData<Int>,
    var scheduleDateDay: MutableLiveData<Int>,
    var scheduleDateHour: MutableLiveData<Int>,
    var scheduleDateMinute: MutableLiveData<Int>,
    var alarmStartTimeHour: MutableLiveData<Int>,
    var alarmStartTimeMinute: MutableLiveData<Int>,
    var fixNotify: MutableLiveData<Boolean>,
    var setAlarm: MutableLiveData<Boolean>,
    var alarmStartTimeType: MutableLiveData<Int>, // 1 : 매일, 2 : 1주일 전, 3 : 3일 전, 4 : 하루 전
    var repeatDay: MutableList<Char> // 반복일정에서 사용하는 반복 요일
) {
    var showDateFormat = MutableLiveData("")

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        scheduleDateYear.value = (year)
        scheduleDateMonth.value = (month)
        scheduleDateDay.value = (day)
        getDateFormat()
    }

    fun setSceduleTime(hour: Int, minute: Int) {
        scheduleDateHour.value = hour
        scheduleDateMinute.value = (minute)
        getDateFormat()
    }

    fun setAlarmStartTime(hour: Int, minute: Int) {
        alarmStartTimeHour.value = (hour)
        alarmStartTimeMinute.value = (minute)
    }

    fun setType(type: Int) {
        this.type.value = type
    }

    fun setRepeatDay(dayOfWeek: Char) {
        if (repeatDay.contains(dayOfWeek)) {
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
        val calendar = Calendar.getInstance()
        scheduleDateYear.value?.let { year -> calendar.set(Calendar.YEAR, year) }
        scheduleDateMonth.value?.let { month -> calendar.set(Calendar.MONTH, month) }
        scheduleDateDay.value?.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }
        val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))

        if (type.value != 3) {
            showDateFormat.value =
                "${scheduleDateYear.value}년 ${scheduleDateMonth.value?.plus(1)}월 ${scheduleDateDay.value}일 ${dayOfWeek}요일" + "\n${scheduleDateHour.value}:${scheduleDateMinute.value}"
        } else {
            repeatDay.sortWith(AboutDay.DayCompare())
            showDateFormat.value =
                repeatDay.toString() + " - " + "\n${scheduleDateHour.value}:${scheduleDateMinute.value}"
        }
        return showDateFormat.value.toString()
    }

    fun getDDay(): String {
        val dDay = getDDayInteger()

        return when {
            dDay > 0 -> {
                "D - $dDay"
            }
            dDay < 0 -> {
                "D + $dDay"
            }
            else -> {
                "D - DAY"
            }
        }
    }

    fun getDDayInteger(): Int {
        val calendar = Calendar.getInstance()
        scheduleDateYear.value?.let { year -> calendar.set(Calendar.YEAR, year) }
        scheduleDateMonth.value?.let { month -> calendar.set(Calendar.MONTH, month) }
        scheduleDateDay.value?.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }
        val today = (System.currentTimeMillis() + (60 * 60 * 9 * 1000)) / (60 * 60 * 24 * 1000)
        val setDateDay = (calendar.time.time.plus((60 * 60 * 9 * 1000)).div((60 * 60 * 24 * 1000)))

        return (setDateDay - today).toInt()
    }

    fun checkRepeatDay(): Boolean {
        return AboutDay.AboutDayOfWeek().checkRepeatDayToday(repeatDay)
    }

}
