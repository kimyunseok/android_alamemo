package com.landvibe.alamemo.model.data.detail

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DetailMemo(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val memoId: Long,
    var type: Int, // 1 : 메모, 2 : 일정
    var icon: String,
    var title: String,
    var scheduleDateYear: Int,
    var scheduleDateMonth: Int,
    var scheduleDateDay: Int,
    var scheduleDateHour: Int,
    var scheduleDateMinute: Int,
    //아래는 세부메모 최대날짜 설정하기 용도.
    var memoScheduleDateYear: Int?,
    var memoScheduleDateMonth: Int?,
    var memoScheduleDateDay: Int?,
    var showDateFormat: String // 보여주는 날짜
)