package com.landvibe.alamemo.util

import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import java.util.*

class MemoUtil {

    fun getDDay(scheduleDateYear: Int, scheduleDateMonth: Int, scheduleDateDay: Int): String {
        val dDay = getDDayInteger(scheduleDateYear, scheduleDateMonth, scheduleDateDay)

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

    fun getDDayInteger(scheduleDateYear: Int, scheduleDateMonth: Int, scheduleDateDay: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, scheduleDateYear)
        calendar.set(Calendar.MONTH, scheduleDateMonth)
        calendar.set(Calendar.DAY_OF_MONTH, scheduleDateDay)

        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24) // 1000(1초) * 60(1분) * 60(1시간) * 24(24시간, 하루)
        val setDateDay = calendar.time.time / (1000 * 60 * 60 * 24)

        return (setDateDay - today).toInt()
    }

    fun getTimeFormat(_hour: Int, _minute: Int): String {
        val hour =
            if(_hour < 10) {
                "0$_hour"
            } else {
                _hour.toString()
            }

        val minute = if(_minute < 10) {
            "0$_minute"
        } else {
            _minute.toString()
        }

        return "${hour}:${minute}"
    }

    fun setMemoDate(memo: Memo?) {
        val calendar = Calendar.getInstance()

        memo?.let {
            calendar.set(Calendar.YEAR, it.scheduleDateYear)
            calendar.set(Calendar.MONTH, it.scheduleDateMonth)
            calendar.set(Calendar.DAY_OF_MONTH, it.scheduleDateDay)

            val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))

            if (it.type != 3) {
                it.showDateFormat =
                    "${it.scheduleDateYear}년 ${it.scheduleDateMonth.plus(1)}월 ${it.scheduleDateDay}일 ${dayOfWeek}요일"
            } else {
                it.repeatDay.sortWith(AboutDay.DayCompare())
                it.showDateFormat = it.repeatDay.toString()
            }
        }
    }

    fun setDetailMemoDate(detailMemo: DetailMemo?) {
        val calendar = Calendar.getInstance()

        detailMemo?.let {
            it.scheduleDateYear.let { year -> calendar.set(Calendar.YEAR, year) }
            it.scheduleDateMonth.let { month -> calendar.set(Calendar.MONTH, month) }
            it.scheduleDateDay.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }
            val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
            it.showDateFormat =
                "${it.scheduleDateYear}년 ${it.scheduleDateMonth.plus(1)}월 ${it.scheduleDateDay}일 ${dayOfWeek}요일"
        }
    }

    fun getMemoTitleInclueTime(memo: Memo): String {
        return if(!memo.scheduleFinish && (memo.type == 2 || memo.type == 3)) {
            MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute) + " " + memo.title
        } else {
            memo.title
        }
    }

    fun getDetailMemoTitleInclueTime(detailMemo: DetailMemo): String {
        return if(detailMemo.type == 2) {
            MemoUtil().getTimeFormat(detailMemo.scheduleDateHour, detailMemo.scheduleDateMinute) + " " + detailMemo.title
        } else {
            detailMemo.title
        }
    }

    fun getDetailMemoMaxDate(detailMemo: DetailMemo): Long {
        val calendar = Calendar.getInstance()
        detailMemo.memoScheduleDateYear?.let { calendar.set(Calendar.YEAR, it) }
        detailMemo.memoScheduleDateMonth?.let { calendar.set(Calendar.MONTH, it) }
        detailMemo.memoScheduleDateDay?.let { calendar.set(Calendar.DAY_OF_MONTH, it) }

        return calendar.time.time
    }


    fun sortMemoList(itemList: MutableList<Memo>) {
        itemList.sortWith(compareBy<Memo> {it.scheduleDateYear}
            .thenBy { it.scheduleDateMonth }
            .thenBy { it.scheduleDateDay }
            .thenBy { it.scheduleDateHour }
            .thenBy { it.scheduleDateMinute }
            .thenBy { it.alarmStartTimeHour }
            .thenBy { it.alarmStartTimeMinute }
        )
    }

    fun sortDetailMemoList(itemList: MutableList<DetailMemo>?) {
        itemList?.sortWith(compareBy<DetailMemo> {it.scheduleDateYear}
            .thenBy { it.scheduleDateMonth }
            .thenBy { it.scheduleDateDay }
            .thenBy { it.scheduleDateHour }
            .thenBy { it.scheduleDateMinute }
        )
    }

}