package com.landvibe.alamemonew.model.data.memo

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.landvibe.alamemonew.util.AboutDay
import java.util.*

@Entity
class Memo (
    @PrimaryKey(autoGenerate = true) var id: Long,
    var type: MutableLiveData<Int>, // 1 : 메모, 2 : 일정, 3 : 반복일정, 4: 종료된 일정(프래그먼트에서 불러올 때만 사용. 실제 4란 type을 저장하지는 않음)
    var icon: MutableLiveData<String>,
    var title: MutableLiveData<String>,
    var scheduleDateYear: MutableLiveData<Int>,
    var scheduleDateMonth: MutableLiveData<Int>,
    var scheduleDateDay: MutableLiveData<Int>,
    var scheduleDateHour: MutableLiveData<Int>,
    var scheduleDateMinute: MutableLiveData<Int>,
    var alarmStartTimeHour: MutableLiveData<Int>,
    var alarmStartTimeMinute: MutableLiveData<Int>,
    var scheduleFinish: MutableLiveData<Boolean>, // 종료됐는지 Check true : 종료, false : 종료 X
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
                "${scheduleDateYear.value}년 ${scheduleDateMonth.value?.plus(1)}월 ${scheduleDateDay.value}일 ${dayOfWeek}요일"
        } else {
            repeatDay.sortWith(AboutDay.DayCompare())
            showDateFormat.value = repeatDay.toString()
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

    fun getTitleInclueTime(): String {
        val hourValue = scheduleDateHour.value?.toInt()
        val minuteValue = scheduleDateMinute.value

        val hour =
            if(hourValue != null && hourValue < 10) {
                "0" + scheduleDateHour.value
            } else {
                scheduleDateHour.value.toString()
            }
        val minute = if(minuteValue != null && minuteValue < 10) {
            "0" + scheduleDateMinute.value
        } else {
            scheduleDateMinute.value.toString()
        }


        return if(type.value == 2 || type.value == 3) {
           "${hour}:${minute} " + title.value
        } else {
            title.value.toString()
        }
    }

    fun setMemoScheduleTimeToday() {
        val calendar = Calendar.getInstance()
        scheduleDateYear.value = calendar.get(Calendar.YEAR)
        scheduleDateMonth.value = calendar.get(Calendar.MONTH)
        scheduleDateDay.value = calendar.get(Calendar.DAY_OF_MONTH)
        scheduleDateHour.value = calendar.get(Calendar.HOUR_OF_DAY)
        scheduleDateMinute.value = calendar.get(Calendar.MINUTE)
    }
}
