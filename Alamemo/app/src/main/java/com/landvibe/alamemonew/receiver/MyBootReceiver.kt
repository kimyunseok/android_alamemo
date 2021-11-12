package com.landvibe.alamemonew.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.handler.AlarmHandler
import com.landvibe.alamemonew.handler.FixNotifyHandler
import com.landvibe.alamemonew.ui.splash.SplashActivity

class MyBootReceiver: BroadcastReceiver() {
    val BOOT_REQUEST_CODE = 1001
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            setBootNotification(context)
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

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
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