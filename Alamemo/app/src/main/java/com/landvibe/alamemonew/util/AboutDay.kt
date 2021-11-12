package com.landvibe.alamemonew.util

import java.lang.Integer.min
import java.util.*
import kotlin.Comparator

/**
* 날짜 관련 기능을 모아놓은 클래스
* */
class AboutDay {

    class AboutDayOfWeek {
        val calendar = Calendar.getInstance()

        fun checkRepeatDayToday(scheduleDays: MutableList<Char>): Boolean {
            val todayDayOfWeek = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
            for(day in scheduleDays) {
                if(day == todayDayOfWeek) {
                    return true
                }
            }
            return false
        }

        fun getDayOfWeek(day: Int): Char {
           return when (day) {
                1 -> {
                    '일'
                }
                2 -> {
                    '월'
                }
                3 -> {
                    '화'
                }
                4 -> {
                    '수'
                }
                5 -> {
                    '목'
                }
                6 -> {
                    '금'
                }
                7 -> {
                    '토'
                }
                else -> {
                    ' '
                }
            }
        }
    }

    //요일 비교 클래스
    class DayCompare: Comparator<Char> {
        override fun compare(p0: Char?, p1: Char?): Int {
            return getDaySequence(p0) - getDaySequence(p1)
        }

        fun getDaySequence(day: Char?): Int {
            return when(day) {
                '일' -> 1
                '월' -> 2
                '화' -> 3
                '수' -> 4
                '목' -> 5
                '금' -> 6
                '토' -> 7
                else -> 0
            }
        }
    }
}