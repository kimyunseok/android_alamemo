package com.landvibe.alamemonew.util

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.model.data.detail.DetailMemo
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.ui.activity.MainActivity

/*
    알람을 보내기 위한 Receiver,
    Recevier에 Extra를 받아오는 것은 https://sunnybong.tistory.com/210 를 참조했다.
 */

class MyReceiver: BroadcastReceiver() {
    lateinit var builder: NotificationCompat.Builder
    lateinit var pendingIntent: PendingIntent

    override fun onReceive(context: Context?, intent: Intent?) {
        val memoId = intent?.getLongExtra("memoId", -1)
        if(memoId != null && context != null && memoId != (-1).toLong()) {
            val memo = AppDataBase.instance.memoDao().getMemoById(memoId)
            val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memoId).toMutableList()
            sortDetailMemoList(detailMemoList)

            initPendingIntent(context, memo)
            initNotificationCompatBuilder(context, memo)
            setBuilderContentText(context, memo, detailMemoList)

        }
    }

    private fun sortDetailMemoList(itemList: MutableList<DetailMemo>?) {
        itemList?.sortWith(compareBy<DetailMemo> {it.scheduleDateYear.value}
            .thenBy { it.scheduleDateMonth.value }
            .thenBy { it.scheduleDateDay.value }
            .thenBy { it.scheduleDateHour.value }
            .thenBy { it.scheduleDateMinute.value }
        )
    }

    private fun initPendingIntent(context: Context, memo: Memo) {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("memoId", memo.id)
            putExtra("memoIcon", memo.icon.value)
            putExtra("memoTitle", memo.title.value)
        }

        pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(mainActivityIntent)
            getPendingIntent(memo.id.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun initNotificationCompatBuilder(context: Context, memo: Memo) {
        builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setContentTitle(memo.title.value)
            .setSmallIcon(R.drawable.iconfinder_icon)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun setBuilderContentText(context: Context, memo: Memo, detailMemoList: MutableList<DetailMemo>) {
        val dDay = memo.getDDayInteger() // dDay계산

        when {
            dDay == 0 -> {
                builder.setContentText(context.getString(R.string.notification_today)
                        + memo.getTimeFormat()
                        + context.getString(R.string.notification_today_time_is)
                        + memo.title.value
                        + context.getString(R.string.notification_today_is_scheduled)
                        + detailMemoList.joinToString("\n") {it.getTitleInclueTime()
                        }
                )
            }
            dDay > 0 -> {
                builder.setContentText(memo.title.value
                        + context.getString(R.string.notification_is)
                        + dDay.toString()
                        + context.getString(R.string.notification_future_is_remain)
                        + detailMemoList.joinToString("\n") {it.getTitleInclueTime()}
                )

            }
            else -> {
                //dDay < 0
                builder.setContentText(memo.title.value
                        + context.getString(R.string.notification_is)
                        + dDay.toString()
                        + context.getString(R.string.notification_past_is_over)
                        + detailMemoList.joinToString("\n") {it.getTitleInclueTime()}
                )
            }
        }
        with(NotificationManagerCompat.from(context)) {
            val notificationID = memo.id.toInt()
            notify(notificationID, builder.build())
        }
    }
}