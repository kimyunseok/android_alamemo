package com.landvibe.alamemo.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.receiver.MyReceiver
import com.landvibe.alamemo.util.AboutDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AlarmHandler {
    lateinit var alarmManager: AlarmManager
    lateinit var pendingIntent: PendingIntent

    fun setUpAllMemoAlarm(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val memoList = AppDataBase.instance.memoDao().getAlarmMemo()
            for(memo in memoList) {
                initPendingIntent(context, memo.id)

                if(memo.type == 2) {
                    //그냥 일정이라면
                    initAlarmManagerForSchedule(context, memo)
                } else {
                    //반복 일정이라면
                    initAlarmManagerForRepeatSchedule(context, memo)
                }
            }
        }
    }

    fun setMemoAlarm(context: Context, memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val memo = AppDataBase.instance.memoDao().getMemoById(memoId)
            initPendingIntent(context, memoId)

            if(memo.type == 2) {
                //그냥 일정이라면
                initAlarmManagerForSchedule(context, memo)
            } else {
                //반복 일정이라면
                initAlarmManagerForRepeatSchedule(context, memo)
            }
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
        val alarmCalendar = Calendar.getInstance()
        val alarmHour = memo.alarmStartTimeHour
        val alarmMinute = memo.alarmStartTimeMinute

        alarmCalendar.set(Calendar.HOUR_OF_DAY, alarmHour)
        alarmCalendar.set(Calendar.MINUTE, alarmMinute)
        alarmCalendar.set(Calendar.SECOND, 0)

        if(memo.alarmStartTimeType == 1) {
            //매일 알람이 설정됐다면, 매일 정해진 시간에 알람을 울려주면 된다.
            checkAlarmTimePass(alarmHour, alarmMinute, alarmCalendar)
        } else {
            // 아니라면 알람이 시작하기로 설정된 날에 맞춰서 알람을 설정하면 된다.
            alarmCalendar.apply {
                set(Calendar.YEAR, memo.scheduleDateYear)
                set(Calendar.MONTH, memo.scheduleDateMonth)
                set(Calendar.DAY_OF_MONTH, memo.scheduleDateDay)
            }

            //타입에 따라서 일 수를 빼준다.
            alarmCalendar.add(Calendar.DAY_OF_MONTH, getDayByAlarmStartType(memo.alarmStartTimeType))

            //만일 뺀 날짜가 오늘보다 이전이라면 오늘 이후로 알람을 설정해준다.
            if(alarmCalendar.timeInMillis < todayCalendar.timeInMillis) {
                alarmCalendar.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR))
                alarmCalendar.set(Calendar.MONTH, todayCalendar.get(Calendar.MONTH))
                alarmCalendar.set(Calendar.DAY_OF_MONTH, todayCalendar.get(Calendar.DAY_OF_MONTH))
                checkAlarmTimePass(alarmHour, alarmMinute, alarmCalendar)
            }
        }

        Log.d("setScheduleAlarm::", "checkTime::" + alarmCalendar.time.toString())

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private suspend fun initAlarmManagerForRepeatSchedule(context: Context, memo: Memo) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val curScheduleCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, memo.scheduleDateYear)
            set(Calendar.MONTH, memo.scheduleDateMonth)
            set(Calendar.DAY_OF_MONTH, memo.scheduleDateDay)
            set(Calendar.HOUR_OF_DAY, memo.alarmStartTimeHour)
            set(Calendar.MINUTE, memo.alarmStartTimeMinute)
        }

        val alarmCalendar = Calendar.getInstance()
        alarmCalendar.timeInMillis = AboutDay.AboutDayOfWeek().findMinTimeAboutDayOfWeekBySpecificTime(memo.repeatDay,
            memo.alarmStartTimeHour,
            memo.alarmStartTimeMinute,
            curScheduleCalendar.timeInMillis
        )

        //반복 일정의 날짜를 찾은 다음 날짜로 바꾼다.
        val yearLivedata = alarmCalendar.get(Calendar.YEAR)
        val monthLivedata = alarmCalendar.get(Calendar.MONTH)
        val dayLivedata = alarmCalendar.get(Calendar.DAY_OF_MONTH)

        AppDataBase.instance.memoDao().modifyMemoDate(memo.id, yearLivedata, monthLivedata, dayLivedata)

        alarmCalendar.set(Calendar.HOUR_OF_DAY, memo.alarmStartTimeHour)
        alarmCalendar.set(Calendar.MINUTE, memo.alarmStartTimeMinute)
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

    private fun checkAlarmTimePass(alarmHour: Int?, alarmMinute: Int?, alarmCalendar: Calendar) {
        val todayCalendar = Calendar.getInstance()

        if(alarmHour != null && alarmMinute != null) {
            //매일 알람을 울려야하는데, 현재 알람 시간을 지난 경우, 알람 달력에 1일을 더 해준다.
            if( todayCalendar.get(Calendar.HOUR_OF_DAY) > alarmHour ||
                (todayCalendar.get(Calendar.HOUR_OF_DAY) == alarmHour && todayCalendar.get(Calendar.MINUTE) >= alarmMinute ) ) {
                alarmCalendar.add(Calendar.DAY_OF_MONTH, 1)
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
        alarmManager.cancel(pendingIntent) // 알람취소
        
        // 상단바에 띄워져 있는 알람도 삭제.
        with(NotificationManagerCompat.from(context)) {
            cancel(memoId.toInt())
        }
    }
}