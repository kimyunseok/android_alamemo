package com.landvibe.alamemo.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.receiver.MyReceiver
import com.landvibe.alamemo.util.AboutDay
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
                    alarmCalendar.add(Calendar.DAY_OF_MONTH, 1)
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

        Log.d("setScheduleAlarm::", "checkTime::" + alarmCalendar.time.toString())

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun initAlarmManagerForRepeatSchedule(context: Context, memo: Memo) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val curScheduleCalendar = Calendar.getInstance().apply {
            memo.scheduleDateYear.value?.let { set(Calendar.YEAR, it) }
            memo.scheduleDateMonth.value?.let { set(Calendar.MONTH, it) }
            memo.scheduleDateDay.value?.let { set(Calendar.DAY_OF_MONTH, it) }
            memo.alarmStartTimeHour.value?.let { set(Calendar.HOUR_OF_DAY, it) }
            memo.alarmStartTimeMinute.value?.let { set(Calendar.MINUTE, it) }
        }

        val alarmCalendar = Calendar.getInstance()
        alarmCalendar.timeInMillis = AboutDay.AboutDayOfWeek().findMinTimeAboutDayOfWeekBySpecificTime(memo, curScheduleCalendar.timeInMillis)

        //반복 일정의 날짜를 찾은 다음 날짜로 바꾼다.
        val yearLivedata = MutableLiveData(alarmCalendar.get(Calendar.YEAR))
        val monthLivedata = MutableLiveData(alarmCalendar.get(Calendar.MONTH))
        val dayLivedata = MutableLiveData(alarmCalendar.get(Calendar.DAY_OF_MONTH))
        AppDataBase.instance.memoDao().modifyMemoDate(memo.id, yearLivedata, monthLivedata, dayLivedata)

        val alarmHour = memo.alarmStartTimeHour.value
        val alarmMinute = memo.alarmStartTimeMinute.value

        alarmHour?.let { alarmCalendar.set(Calendar.HOUR_OF_DAY, it) }
        alarmMinute?.let { alarmCalendar.set(Calendar.MINUTE, it) }
        alarmCalendar.set(Calendar.SECOND, 1)

        Log.d("setRepeatAlarm::", "checkTime::" + alarmCalendar.time.toString())

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
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