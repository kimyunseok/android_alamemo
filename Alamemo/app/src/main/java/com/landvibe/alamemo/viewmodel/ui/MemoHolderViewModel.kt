package com.landvibe.alamemo.viewmodel.ui

import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.util.AboutDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MemoHolderViewModel(val memo: Memo, val memoRepository: MemoRepository) {
    fun getDateFormat(): String {
        val calendar = Calendar.getInstance()
        memo.scheduleDateYear.let { year -> calendar.set(Calendar.YEAR, year) }

        memo.scheduleDateMonth.let { month -> calendar.set(Calendar.MONTH, month) }

        memo.scheduleDateDay.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }

        val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))

        if (memo.type != 3) {
            memo.showDateFormat =
                "${memo.scheduleDateYear}년 ${memo.scheduleDateMonth.plus(1)}월 ${memo.scheduleDateDay}일 ${dayOfWeek}요일"
        } else {
            memo.repeatDay.sortWith(AboutDay.DayCompare())
            memo.showDateFormat = memo.repeatDay.toString()
        }
        return memo.showDateFormat
    }

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
        memo.scheduleDateYear.let { year -> calendar.set(Calendar.YEAR, year) }
        memo.scheduleDateYear.let { month -> calendar.set(Calendar.MONTH, month) }
        memo.scheduleDateYear.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24) // 1000(1초) * 60(1분) * 60(1시간) * 24(24시간, 하루)
        val setDateDay = calendar.time.time / (1000 * 60 * 60 * 24)

        return (setDateDay - today).toInt()
    }


    fun checkRepeatDay(): Boolean {
        memo.repeatDay.let {
            return AboutDay.AboutDayOfWeek().checkRepeatDayToday(it)
        }
        return false
    }

    fun getTitleInclueTime(): String {
        return if(!memo.scheduleFinish && (memo.type == 2 || memo.type == 3)) {
            getTimeFormat() + " " + memo.title
        } else {
            memo.title.toString()
        }
    }

    fun getTimeFormat(): String {
        val hourValue = memo.scheduleDateHour
        val minuteValue = memo.scheduleDateMinute

        val hour =
            if(hourValue < 10) {
                "0" + memo.scheduleDateHour
            } else {
                memo.scheduleDateHour.toString()
            }
        val minute = if(minuteValue < 10) {
            "0" + memo.scheduleDateMinute
        } else {
            memo.scheduleDateMinute.toString()
        }

        return "${hour}:${minute}"
    }

    fun finishMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            memoRepository.modifyMemo(
                id = memo.id,
                type = memo.type,
                icon = memo.icon,
                title = memo.title,
                scheduleDateYear = memo.scheduleDateYear,
                scheduleDateMonth = memo.scheduleDateMonth,
                scheduleDateDay = memo.scheduleDateDay,
                scheduleDateHour = memo.scheduleDateHour,
                scheduleDateMinute = memo.scheduleDateMinute,
                alarmStartTimeHour = memo.alarmStartTimeHour,
                alarmStartTimeMinute = memo.alarmStartTimeMinute,
                scheduleFinish = true,
                fixNotify = false,
                setAlarm = false,
                repeatDay = memo.repeatDay,
                alarmStartTimeType = 1
            )
        }
    }
}