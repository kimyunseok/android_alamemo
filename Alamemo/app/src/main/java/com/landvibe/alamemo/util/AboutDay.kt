package com.landvibe.alamemo.util

import java.util.*

/**
* 날짜 관련 기능을 모아놓은 클래스
* */
class AboutDay {

    class AboutDayOfWeek {
        fun checkRepeatDayToday(scheduleDays: MutableList<Char>): Boolean {
            val calendar = Calendar.getInstance()

            val todayDayOfWeek = getDayOfWeekByInteger(calendar.get(Calendar.DAY_OF_WEEK))
            for(day in scheduleDays) {
                if(day == todayDayOfWeek) {
                    return true
                }
            }
            return false
        }

        fun getDayOfWeekByInteger(dayOfWeek: Int): Char {
            return when (dayOfWeek) {
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

        fun getDayOfWeekByDate(year: Int?, month: Int?, dayOfMonth: Int?): Char {
            val calendar = Calendar.getInstance()
            year?.let { calendar.set(Calendar.YEAR, it) }
            month?.let { calendar.set(Calendar.MONTH, it) }
            dayOfMonth?.let { calendar.set(Calendar.DAY_OF_MONTH, it) }

           return when (calendar.get(Calendar.DAY_OF_WEEK)) {
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

        //가지고있는 반복요일에 대해 다시 알람설정. - specificTime 이후에 날짜로 설정
        fun findMinTimeAboutDayOfWeekBySpecificTime(repeatDay: MutableList<Char>, alarmStartTimeHour: Int?,
                                                    alarmStartTimeMinute: Int?, specificTime: Long): Long {
            val alarmCalendar = Calendar.getInstance().apply {
                timeInMillis = specificTime
                add(Calendar.DAY_OF_MONTH, 8)
                set(Calendar.SECOND, 0)
            }

            var checkCalendar: Calendar

            for(dayOfWeek in repeatDay) {
                val dayOfWeekToInt = DayCompare().getDaySequence(dayOfWeek)

                checkCalendar = Calendar.getInstance()
                checkCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeekToInt)
                alarmStartTimeHour?.let { checkCalendar.set(Calendar.HOUR_OF_DAY, it) }
                alarmStartTimeMinute?.let { checkCalendar.set(Calendar.MINUTE, it) }
                checkCalendar.set(Calendar.SECOND, 0)

                //오늘보다 이르다면 7일을 더해준다
                if (checkCalendar.timeInMillis < System.currentTimeMillis()) {
                    checkCalendar.add(Calendar.DAY_OF_MONTH, 7)
                }

                //특정 시간보다 작다면 시간 변경
                if(checkCalendar.timeInMillis < alarmCalendar.timeInMillis) {
                    alarmCalendar.time = checkCalendar.time
                }
            }

            return alarmCalendar.timeInMillis
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