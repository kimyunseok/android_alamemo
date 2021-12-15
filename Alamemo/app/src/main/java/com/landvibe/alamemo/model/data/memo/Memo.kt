package com.landvibe.alamemo.model.data.memo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo (
    @PrimaryKey(autoGenerate = true) var id: Long,
    var type: Int, // 1 : 메모, 2 : 일정, 3 : 반복일정, 4: 종료된 일정(프래그먼트에서 불러올 때만 사용. 실제 4란 type을 저장하지는 않음)
    var icon: String,
    var title: String,
    var scheduleDateYear: Int,
    var scheduleDateMonth: Int,
    var scheduleDateDay: Int,
    var scheduleDateHour: Int,
    var scheduleDateMinute: Int,
    var alarmStartTimeHour: Int,
    var alarmStartTimeMinute: Int,
    var scheduleFinish: Boolean, // 종료됐는지 Check true : 종료, false : 종료 X
    var fixNotify: Boolean,
    var setAlarm: Boolean,
    var alarmStartTimeType: Int, // 1 : 매일, 2 : 1주일 전, 3 : 3일 전, 4 : 하루 전
    var repeatDay: MutableList<Char>, // 반복일정에서 사용하는 반복 요일
)