package com.landvibe.alamemo.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemo.R
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.ui.splash.SplashActivity
/**
 * 휴대폰이 껐다 켜졌을 때,
 * 앱이 업데이트 됐을 때
 * 알람을 자동으로 설정해주는 브로드 캐스트 수신자
 */
class MyBootAndUpdateReceiver: BroadcastReceiver() {
    val BOOT_REQUEST_CODE = 1001
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {
                //부팅완료일 경우만, 해당 메시지 보낸다.
                setBootNotification(context)
            }
            setUpFixNotifyAndAlarm(context)
        }
    }

    private fun setUpFixNotifyAndAlarm(context: Context) {
        AlarmHandler().setUpAllMemoAlarm(context)
        FixNotifyHandler().setUpAllFixNotify(context)
    }

    private fun setBootNotification(context: Context) {
        val splashIntent = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, BOOT_REQUEST_CODE, splashIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, context.getString(R.string.alarm_channel_id))
            .setSmallIcon(R.drawable.iconfinder_icon)
            .setContentTitle(context.getString(R.string.memo_emoji) + " " + context.getString(R.string.app_name))
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setContentText(context.getString(R.string.notification_boot))

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }
}