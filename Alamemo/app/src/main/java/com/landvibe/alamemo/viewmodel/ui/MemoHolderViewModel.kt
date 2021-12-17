package com.landvibe.alamemo.viewmodel.ui

import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.util.AboutDay
import com.landvibe.alamemo.util.MemoUtil

class MemoHolderViewModel(val memo: Memo) {

    fun getDDay(): String {
        return MemoUtil().getDDay(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay)
    }

    fun getDDayInteger(): Int {
        return MemoUtil().getDDayInteger(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay)
    }

    fun checkRepeatDay(): Boolean {
        return AboutDay.AboutDayOfWeek().checkRepeatDayToday(memo.repeatDay)
    }

    fun getTitleInclueTime(): String {
        return MemoUtil().getMemoTitleInclueTime(memo)
    }

    fun showDateFormat(): String {
        return if (memo.type != 3) {
            MemoUtil().getScheduleDateFormat(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay)
        } else {
            MemoUtil().getRepeatScheduleDateFormat(memo.repeatDay)
        }
    }

    fun alreadyPassedDate(): Boolean {
        return (getDDayInteger() < 0)
    }

}