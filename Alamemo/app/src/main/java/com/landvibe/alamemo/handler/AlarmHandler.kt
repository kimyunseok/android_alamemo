package com.landvibe.alamemo.handler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun initAlarmManagerForRepeatSchedule(context: Context, memo: Memo) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmCalendar = Calendar.getInstance().apply { add(Calendar.YEAR, 1) } //
        var checkCalendar: Calendar

        val alarmHour = memo.alarmStartTimeHour.value
        val alarmMinute = memo.alarmStartTimeMinute.value

        //가지고있는 반복요일에 대해 알람설정
        //반복일정은 울리고 나서 다음 최소 날짜를 찾아서 알람을 설정한다.
        for(dayOfWeek in memo.repeatDay) {

            val dayOfWeekToInt = AboutDay.DayCompare().getDaySequence(dayOfWeek)

            checkCalendar = Calendar.getInstance()
            checkCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeekToInt)
            alarmHour?.let { checkCalendar.set(Calendar.HOUR_OF_DAY, it) }
            alarmMinute?.let { checkCalendar.set(Calendar.MINUTE, it) }
            checkCalendar.set(Calendar.SECOND, 59)

            //오늘보다 이르다면 7일을 더해준다
            if(checkCalendar.timeInMillis < System.currentTimeMillis()) {
                checkCalendar.add(Calendar.DAY_OF_MONTH, 7)
            }

            if(checkCalendar.timeInMillis < alarmCalendar.timeInMillis) {
                alarmCalendar.time = checkCalendar.time
            }
        }

        val memoYear = memo.scheduleDateYear.value
        val memoMonth = memo.scheduleDateMonth.value
        val memoDay = memo.scheduleDateDay.value

        //반복 일정의 날짜를 찾은 다음 날짜로 바꾼다.
        val yearLivedata = MutableLiveData(alarmCalendar.get(Calendar.YEAR))
        val monthLivedata = MutableLiveData(alarmCalendar.get(Calendar.YEAR))
        val dayLivedata = MutableLiveData(alarmCalendar.get(Calendar.YEAR))
        AppDataBase.instance.memoDao().modifyMemoDate(memo.id, yearLivedata, monthLivedata, dayLivedata)

        alarmHour?.let { alarmCalendar.set(Calendar.HOUR_OF_DAY, it) }
        alarmMinute?.let { alarmCalendar.set(Calendar.MINUTE, it) }
        alarmCalendar.set(Calendar.SECOND, 1)

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