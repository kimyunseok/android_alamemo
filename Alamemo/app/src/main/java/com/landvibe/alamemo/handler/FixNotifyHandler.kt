package com.landvibe.alamemo.handler

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemo.R
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.util.MemoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FixNotifyHandler {
    lateinit var builder: NotificationCompat.Builder
    lateinit var pendingIntent: PendingIntent

    fun setUpAllFixNotify(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val memoList = AppDataBase.instance.memoDao().getFixNotifyMemo().toMutableList()

            for(memo in memoList) {
                val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id).toMutableList()

                initPendingIntent(context, memo)
                initNotificationCompatBuilder(context, memo, detailMemoList)
            }
        }
    }

    //개별로 상단바 고정 설정해주기.
    fun setMemoFixNotify(context: Context, memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val memo = AppDataBase.instance.memoDao().getMemoById(memoId)
            val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id).toMutableList()

            initPendingIntent(context, memo)
            initNotificationCompatBuilder(context, memo, detailMemoList)
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
            addParentStack(MainActivity::class.java)
            addNextIntentWithParentStack(mainActivityIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getPendingIntent(memo.id.toInt(), PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                getPendingIntent(memo.id.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
            }
            //요청 코드를 메모Id를 Int로 변환해서 쓰면 위험하긴 하지만 21억번 메모를 할 일은 없을 것 같다...
        }
    }

    private fun initNotificationCompatBuilder(context: Context, memo: Memo, detailMemoList: MutableList<DetailMemo>) {
        builder = NotificationCompat.Builder(context, context.getString(R.string.fix_channel_id))
            .setContentTitle(memo.icon + " " + memo.title)
            .setSmallIcon(R.drawable.iconfinder_icon)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setSilent(true)

        //상단 바 고정 시 날짜를 표기해주는 용도. 메모는 표기하지 않는다.
        var contentText = when {
            memo.type == 1 -> {
                ""
            }
            memo.type != 3 -> {
                MemoUtil().getScheduleDateFormat(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay) + " " + MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute)
            }
            else -> {
                MemoUtil().getRepeatScheduleDateFormat(memo.repeatDay) + " " + MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute)
            }
        }

        MemoUtil().sortDetailMemoList(detailMemoList)

        if(memo.type != 2 && detailMemoList.isEmpty().not()) {
            //메모, 반복일정의 경우에는 시간표시가 안되므로 '-'로 구분지어줘야 한다.
            contentText += context.getString(
                R.string.notification_fix_notify_slash) +
                    detailMemoList.joinToString(context.getString(R.string.notification_fix_notify_slash_include_line_enter)) { it.icon + " " + it.title }

        } else if(detailMemoList.isEmpty().not()){
            contentText += "\n"
            contentText +=
                detailMemoList.joinToString("\n") {
                    MemoUtil().getScheduleDateFormat(it.scheduleDateYear, it.scheduleDateMonth, it.scheduleDateDay) + " " +
                    MemoUtil().getTimeFormat(it.scheduleDateHour, it.scheduleDateMinute) +
                            " - " + it.icon +
                            " " + it.title }
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
}