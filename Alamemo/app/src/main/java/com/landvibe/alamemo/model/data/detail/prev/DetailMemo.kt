package com.landvibe.alamemo.model.data.detail.prev

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.landvibe.alamemo.util.AboutDay
import java.util.*

@Entity
class DetailMemo(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val memoId: Long,
    var type: MutableLiveData<Int>, // 1 : 메모, 2 : 일정
    var icon: MutableLiveData<String>,
    var title: MutableLiveData<String>,
    var scheduleDateYear: MutableLiveData<Int>,
    var scheduleDateMonth: MutableLiveData<Int>,
    var scheduleDateDay: MutableLiveData<Int>,
    var scheduleDateHour: MutableLiveData<Int>,
    var scheduleDateMinute: MutableLiveData<Int>,
    //아래는 세부메모 최대날짜 설정하기 용도.
    var memoScheduleDateYear: Int?,
    var memoScheduleDateMonth: Int?,
    var memoScheduleDateDay: Int?,
) {
    var showDateFormat = MutableLiveData("")

    fun getDDay(): String {
        val dDay = getDDayInteger()

        return when {
            dDay > 0 -> {
                "D - $dDay"
            }
            dDay < 0 -> {
                "D + ${0 - dDay}"
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

    fun getTitleInclueTime(): String {
        return if(type.value == 2) {
            getTimeFormat() + " " + title.value
        } else {
            title.value.toString()
        }
    }

    fun getTimeFormat(): String {
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

        return "${hour}:${minute}"
    }

}