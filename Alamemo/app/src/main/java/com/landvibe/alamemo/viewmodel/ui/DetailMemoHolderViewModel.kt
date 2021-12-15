package com.landvibe.alamemo.viewmodel.ui

import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.util.MemoUtil

class DetailMemoHolderViewModel(val detailMemo: DetailMemo) {

    fun getDDay(): String {
        return MemoUtil().getDDay(detailMemo.scheduleDateYear, detailMemo.scheduleDateMonth, detailMemo.scheduleDateDay)
    }

    fun getDDayInteger(): Int {
        return MemoUtil().getDDayInteger(detailMemo.scheduleDateYear, detailMemo.scheduleDateMonth, detailMemo.scheduleDateDay)
    }

    fun getTitleInclueTime(): String {
        return MemoUtil().getDetailMemoTitleInclueTime(detailMemo)
    }

    fun showDateFormat(): String {
        return MemoUtil().getScheduleDateFormat(detailMemo.scheduleDateYear, detailMemo.scheduleDateMonth, detailMemo.scheduleDateDay)
    }

}