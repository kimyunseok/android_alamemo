package com.landvibe.alamemonew.util

class DayCompare: Comparator<Char> {
    override fun compare(p0: Char?, p1: Char?): Int {
        return getDaySequence(p1) - getDaySequence(p0)
    }

    private fun getDaySequence(day: Char?): Int {
        return when(day) {
            '월' -> 7
            '화' -> 6
            '수' -> 5
            '목' -> 4
            '금' -> 3
            '토' -> 2
            '일' -> 1
            else -> 0
        }
    }
}