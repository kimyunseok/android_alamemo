package com.landvibe.alamemo.receiver

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemo.R
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.util.MemoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
    알람을 보내기 위한 Receiver,
    Recevier에 Extra를 받아오는 것은 https://sunnybong.tistory.com/210 를 참조했다.
 */

class MyReceiver: BroadcastReceiver() {
    lateinit var builder: NotificationCompat.Builder
    lateinit var pendingIntent: PendingIntent

    override fun onReceive(context: Context?, intent: Intent?) {
        CoroutineScope(Dispatchers.Main).launch {
            val memoId = intent?.getLongExtra("memoId", -1)
            if(memoId != null && context != null && memoId != (-1).toLong()) {
                val memo = AppDataBase.instance.memoDao().getMemoById(memoId)
                val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memoId).toMutableList()
                MemoUtil().sortDetailMemoList(detailMemoList)

                initPendingIntent(context, memo)
                initNotificationCompatBuilder(context, memo)
                setBuilderContentText(context, memo, detailMemoList)

                if(memo.type == 3) {
                    //반복 일정이라면 알람설정을 다시 SET해준다.
                    setUpRepeatMemoAlarm(context, memo)
                }
            }
        }
    }

    private fun initPendingIntent(context: Context, memo: Memo) {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("memoId", memo.id)
            putExtra("memoIcon", memo.icon)
            putExtra("memoTitle", memo.title)
            putExtra("memoType", memo.type)
        }

        pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(mainActivityIntent)
            getPendingIntent(memo.id.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun initNotificationCompatBuilder(context: Context, memo: Memo) {
        builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setContentTitle(memo.icon + " " +memo.title)
            .setSmallIcon(R.drawable.iconfinder_icon)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun setBuilderContentText(context: Context, memo: Memo, detailMemoList: MutableList<DetailMemo>) {
        val dDay = MemoUtil().getDDayInteger(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay) // dDay계산

        val detailListText = detailMemoList.joinToString("\n") {
            if (memo.type == 2) {
                // 일정이라면 날짜도 보여준다.
                MemoUtil().getScheduleDateFormat(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay)+
                        " " + MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute) + " - " + it.icon + " " + it.title
            } else {
                it.icon + " " + it.title
            }
        }

        when {
            dDay == 0 -> {
                builder.setContentText(context.getString(R.string.notification_today) + " "
                        + MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute)
                        + context.getString(R.string.notification_today_time_is) + " "
                        + memo.title
                        + context.getString(R.string.notification_today_is_scheduled)
                        + detailListText
                )
            }
            dDay > 0 -> {
                builder.setContentText(memo.title
                        + context.getString(R.string.notification_is) + " "
                        + dDay.toString()
                        + context.getString(R.string.notification_future_is_remain)
                        + detailListText
                )

            }
            else -> {
                //dDay < 0
                builder.setContentText(memo.title
                        + context.getString(R.string.notification_is) + " "
                        + dDay.toString()
                        + context.getString(R.string.notification_past_is_over)
                        + detailListText
                )
            }
        }
        with(NotificationManagerCompat.from(context)) {
            val notificationID = memo.id.toInt()
            notify(notificationID, builder.build())
        }
    }

    private fun setUpRepeatMemoAlarm(context: Context, memo: Memo) {
        AlarmHandler().setMemoAlarm(context, memo.id)
    }

}