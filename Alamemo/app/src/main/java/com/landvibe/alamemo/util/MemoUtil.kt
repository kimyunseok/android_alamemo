package com.landvibe.alamemo.util

import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun finishScheduleBeforeCurrentTime(itemList: MutableList<Memo>) {
        //일정 중에서 오늘날짜보다 지난것들은 종료처리.
        val today = System.currentTimeMillis()
        for(data in itemList) {
            val calendar = Calendar.getInstance()
            data.scheduleDateYear.let { calendar.set(Calendar.YEAR, it) }
            data.scheduleDateMonth.let { calendar.set(Calendar.MONTH, it) }
            data.scheduleDateDay.let { calendar.set(Calendar.DAY_OF_MONTH, it) }
            //시간은 상관없이 당일의 모든 일정을 보여주도록 하기위해 비교하는 날의 시간은 23:59분으로 맞춤.
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val checkDay = calendar.time.time

            if(checkDay < today) {
                AppDataBase.instance.memoDao().setMemoFinish(data.id)
                itemList.remove(data)
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

    fun getScheduleDateFormat(_year: Int?, _month: Int?, _dayOfMonth: Int?): String {
        val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeekByDate(_year, _month, _dayOfMonth)

        return "${_year}년 ${_month?.plus(1)}월 ${_dayOfMonth}일 ${dayOfWeek}요일"
    }

    fun getRepeatScheduleDateFormat(_repeatDay: MutableList<Char>): String {
        _repeatDay.sortWith(AboutDay.DayCompare())
        return _repeatDay.toString()
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

    fun getMemoTitleInclueTime(memo: Memo): String {
        return if(memo.type != 4 && (memo.type == 2 || memo.type == 3)) {
            MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute) + " " + memo.title
        } else {
            memo.title
        }
    }

    fun getDetailMemoTitleIncludeTime(detailMemo: DetailMemo): String {
        return if(detailMemo.type == 2) {
            MemoUtil().getTimeFormat(detailMemo.scheduleDateHour, detailMemo.scheduleDateMinute) + " " + detailMemo.title
        } else {
            detailMemo.title
        }
    }

    fun sortMemoList(itemList: MutableList<Memo>, type: Int) {
        itemList.sortWith(compareBy<Memo> {it.scheduleDateYear}
            .thenBy { it.scheduleDateMonth }
            .thenBy { it.scheduleDateDay }
            .thenBy { it.scheduleDateHour }
            .thenBy { it.scheduleDateMinute }
            .thenBy { it.alarmStartTimeHour }
            .thenBy { it.alarmStartTimeMinute }
        )

        //종료 일정은 최신순으로 보여준다.
        if(type == 4) {
            itemList.reverse()
        }
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