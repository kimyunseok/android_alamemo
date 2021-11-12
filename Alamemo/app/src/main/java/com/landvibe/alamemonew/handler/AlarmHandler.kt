package com.landvibe.alamemonew.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.receiver.MyReceiver
import java.util.*

class AlarmHandler {
    lateinit var alarmManager: AlarmManager
    lateinit var pendingIntent: PendingIntent

    fun setUpAllMemoAlarm(context: Context) {
        val memoList = AppDataBase.instance.memoDao().getAlarmMemo()

        for(memo in memoList) {
            setMemoAlarm(context, memo)
        }
    }

    fun setMemoAlarm(context: Context, memo: Memo) {

        initPendingIntent(context, memo.id)

        if(memo.type.value == 2) {
            //그냥 일정이라면
            initAlarmManagerForSchedule(context, memo)
        } else {
            //반복 일정이라면
            initAlarmManagerForRepeatSchedule(context, memo)
        }
    }

    private fun initPendingIntent(context: Context, memoId: Long) {
        val receiverIntent = Intent(context, MyReceiver::class.java).putExtra("memoId", memoId)

        pendingIntent = PendingIntent.getBroadcast(
            context, memoId.toInt(), receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun initAlarmManagerForSchedule(context: Context, memo: Memo) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val todayCalendar = Calendar.getInstance()
        val alarmCalendar = Calendar.getInstance().apply {
            memo.scheduleDateYear.value?.let { year -> set(Calendar.YEAR, year) }
            memo.scheduleDateMonth.value?.let { month -> set(Calendar.MONTH, month) }
            memo.scheduleDateDay.value?.let { day -> set(Calendar.DAY_OF_MONTH, day) }
        }

        if(memo.alarmStartTimeType.value == 1) {
            val alarmHour = memo.alarmStartTimeHour.value
            val alarmMinute = memo.alarmStartTimeMinute.value
            if(alarmHour != null && alarmMinute != null) {
                //매일 알람이 설정됐다면, 매일 정해진 시간에 알람을 울려주면 된다.
                if(todayCalendar.get(Calendar.HOUR_OF_DAY) < alarmHour ||
                    (todayCalendar.get(Calendar.HOUR_OF_DAY) == alarmHour &&
                            todayCalendar.get(Calendar.MINUTE) < alarmMinute) ) {
                    alarmCalendar.timeInMillis = System.currentTimeMillis()
                } else {
                    alarmCalendar.timeInMillis = System.currentTimeMillis() + (1000 * 60 * 60 * 24)
                }
            }
        } else {
            // 아니라면 알람이 시작하기로 설정된 날에 맞춰서 알람을 설정하면 된다.
            memo.alarmStartTimeType.value?.let { alarmStartTimeType ->
                //타입에 따라서 일 수를 빼준다.
                alarmCalendar.add(Calendar.DAY_OF_MONTH, getDayByAlarmStartType(alarmStartTimeType))
            }
        }

        memo.alarmStartTimeHour.value?.let { hour -> alarmCalendar.set(Calendar.HOUR_OF_DAY, hour) }
        memo.alarmStartTimeMinute.value?.let { minute -> alarmCalendar.set(Calendar.MINUTE, minute) }
        alarmCalendar.set(Calendar.SECOND, 1)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun initAlarmManagerForRepeatSchedule(context: Context, memo: Memo) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val todayCalendar = Calendar.getInstance()
        val alarmCalendar = Calendar.getInstance().apply {
            memo.scheduleDateYear.value?.let { year -> set(Calendar.YEAR, year) }
            memo.scheduleDateMonth.value?.let { month -> set(Calendar.MONTH, month) }
            memo.scheduleDateDay.value?.let { day -> set(Calendar.DAY_OF_MONTH, day) }
        }

        if(memo.alarmStartTimeType.value == 1) {
            val alarmHour = memo.alarmStartTimeHour.value
            val alarmMinute = memo.alarmStartTimeMinute.value
            if(alarmHour != null && alarmMinute != null) {
                //매일 알람이 설정됐다면, 매일 정해진 시간에 알람을 울려주면 된다.
                if(todayCalendar.get(Calendar.HOUR_OF_DAY) < alarmHour ||
                    (todayCalendar.get(Calendar.HOUR_OF_DAY) == alarmHour &&
                            todayCalendar.get(Calendar.MINUTE) < alarmMinute) ) {
                    alarmCalendar.timeInMillis = System.currentTimeMillis()
                } else {
                    alarmCalendar.timeInMillis = System.currentTimeMillis() + (1000 * 60 * 60 * 24)
                }
            }
        }

        memo.alarmStartTimeHour.value?.let { hour -> alarmCalendar.set(Calendar.HOUR_OF_DAY, hour) }
        memo.alarmStartTimeMinute.value?.let { minute -> alarmCalendar.set(Calendar.MINUTE, minute) }
        alarmCalendar.set(Calendar.SECOND, 1)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7, // 1주일 단위로 반복
            pendingIntent
        )
    }

    //알람 시작일에 따른 값을 return. 1 : 매일, 2 : 1주일 전, 3 : 3일 전, 4 : 하루 전
    private fun getDayByAlarmStartType(type: Int): Int {
        return when(type) {
            2 -> {
                -7
            }
            3 -> {
                -3
            }
            4 -> {
                -1
            }
            else -> {
                0
            }
        }
    }

    fun cancelAlarm(context: Context, memoId: Long) {
        alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            memoId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}