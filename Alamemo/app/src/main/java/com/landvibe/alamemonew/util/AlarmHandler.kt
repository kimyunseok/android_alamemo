package com.landvibe.alamemonew.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.model.data.memo.Memo
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

    private fun setMemoAlarm(context: Context, memo: Memo) {

        initPendingIntent(context, memo.id.toInt())
        initAlarmManager(context, memo)
    }

    private fun initPendingIntent(context: Context, memoId: Int) {
        val receiverIntent = Intent(context, MyReceiver::class.java).putExtra("memoId", memoId)

        pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(receiverIntent)
            getPendingIntent(memoId, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun initAlarmManager(context: Context, memo: Memo) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val todayCalendar = Calendar.getInstance()
        val alarmCalendar = Calendar.getInstance().apply {
            memo.scheduleDateYear.value?.let { year -> set(Calendar.YEAR, year) }
            memo.scheduleDateMonth.value?.let { month -> set(Calendar.YEAR, month) }
            memo.scheduleDateDay.value?.let { day -> set(Calendar.YEAR, day) }
            memo.alarmStartTimeHour.value?.let { hour -> set(Calendar.YEAR, hour) }
            memo.alarmStartTimeMinute.value?.let { minute -> set(Calendar.YEAR, minute) }
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
                alarmCalendar.time.time -= getDayByAlarmStartType(alarmStartTimeType)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    //알람 시작일에 따른 값을 return. 1 : 매일, 2 : 1주일 전, 3 : 3일 전, 4 : 하루 전
    private fun getDayByAlarmStartType(type: Int) : Long {
        return when(type) {
            2 -> {
                (1000 * 60 * 60 * 24) * 7
            }
            3 -> {
                (1000 * 60 * 60 * 24) * 3
            }
            4 -> {
                (1000 * 60 * 60 * 24) * 1
            }
            else -> {
                0
            }
        }
    }

    fun cancelAlarm(context: Context, memoId: Int) {
        alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            memoId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}