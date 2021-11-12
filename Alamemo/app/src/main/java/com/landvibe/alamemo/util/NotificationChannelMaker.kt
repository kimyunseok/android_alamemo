package com.landvibe.alamemo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.landvibe.alamemo.R

/**
 * Android 8.0 이상에서 알림을 제공하려면 먼저 NotificationChannel
 * 인스턴스를 createNotificationChannel()에 전달하여 앱의 알림 채널을 시스템에 등록해야 합니다.
 * https://developer.android.com/training/notify-user/build-notification?hl=ko
 */
class NotificationChannelMaker {

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                context.getString(R.string.notification_channel_id),
                name,
                importance)
                .apply {
                    description = descriptionText
                    setShowBadge(false) // 뱃지 안보이게 설정
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}