package com.landvibe.alamemonew.model.data.memo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Memo (
    @PrimaryKey(autoGenerate = true) var id: Long,
    var type: Int, // 1 : 메모, 2 : 일정, 3 : 반복일정, 4 : 종료된 일정
    var icon: String,
    var title: String,
    var scheduleDate: Date,
    var alarmStartTime: Date,
    var fixNotify: Boolean,
    var setAlarm: Boolean,
    var repeatDay: String, // 반복일정에서 사용하는 반복 요일
    var alarmStartTimeType: Int // 1 : 매일, 2 : 1주일 전, 3 : 3일 전, 4 : 하루 전
)