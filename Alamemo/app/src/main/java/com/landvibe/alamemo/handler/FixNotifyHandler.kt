package com.landvibe.alamemo.handler

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemo.R
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.ui.activity.MainActivity

class FixNotifyHandler {
    lateinit var builder: NotificationCompat.Builder
    lateinit var pendingIntent: PendingIntent

    fun setUpAllFixNotify(context: Context) {
        val memoList = AppDataBase.instance.memoDao().getFixNotifyMemo().toMutableList()

        for(memo in memoList) {
            setMemoFixNotify(context, memo)
        }
    }

    //개별로 상단바 고정 설정해주기.
    fun setMemoFixNotify(context: Context, memo: Memo) {
        val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id).toMutableList()

        initPendingIntent(context, memo)
        initNotificationCompatBuilder(context, memo, detailMemoList)
    }

    private fun initPendingIntent(context: Context, memo: Memo) {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("memoId", memo.id)
            putExtra("memoIcon", memo.icon.value)
            putExtra("memoTitle", memo.title.value)
            putExtra("memoType", memo.type.value)
        }

        pendingIntent = TaskStackBuilder.create(context).run {
            addParentStack(MainActivity::class.java)
            addNextIntentWithParentStack(mainActivityIntent)
            getPendingIntent(memo.id.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
            //요청 코드를 메모Id를 Int로 변환해서 쓰면 위험하긴 하지만 21억번 메모를 할 일은 없을 것 같다...
        }
    }

    private fun initNotificationCompatBuilder(context: Context, memo: Memo, detailMemoList: MutableList<DetailMemo>) {
        builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
            .setContentTitle(memo.icon.value + " " + memo.title.value)
            .setSmallIcon(R.drawable.iconfinder_icon)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)

        //상단 바 고정 시 날짜를 표기해주는 용도. 메모는 표기하지 않는다.
        var contentText = if(memo.type.value == 1) {
            ""
        } else {
            memo.getDateFormat() + " " + memo.getTimeFormat()
        }

        sortDetailMemoList(detailMemoList)

        if(memo.type.value != 2 && detailMemoList.isEmpty().not()) {
            contentText += "\n\n"
            //메모, 반복일정의 경우에는 시간표시가 안되므로 '-'로 구분지어줘야 한다.
            contentText += context.getString(
                R.string.notification_fix_notify_slash) +
                    detailMemoList.joinToString(context.getString(R.string.notification_fix_notify_slash_include_line_enter)) { it.icon.value.toString() + " " + it.title.value.toString() }

        } else if(detailMemoList.isEmpty().not()){
            contentText += "\n\n"
            contentText +=
                detailMemoList.joinToString("\n") { it.getDateFormat() + " - " + it.icon.value.toString() + " " + it.getTitleInclueTime() }
        }

        builder.setContentText(contentText)

        with(NotificationManagerCompat.from(context)) {
            val fixNotifyId = 0 - memo.id.toInt() // 그냥 알람과 다른 id 사용을 위해 상단바 고정 id는 -id값 으로 설정
            notify(fixNotifyId, builder.build())
        }
    }

    fun cancelFixNotify(context: Context, memoId: Long) {
        with(NotificationManagerCompat.from(context)) {
            val fixNotifyId = 0 - memoId
            cancel(fixNotifyId.toInt())
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
}