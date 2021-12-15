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
    var scheduleDateMinute: Int
)