package com.fitcore.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalTime

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val now = LocalTime.now()
        val start = LocalTime.of(10, 0)
        val end = LocalTime.of(23, 0)

        if (now.isAfter(start) && now.isBefore(end)) {
            showNotification(
                title = "Hydration Nudge",
                content = "Time to drink some water! Keep that 3.5L goal in sight."
            )
        }
        
        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "water_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Water Reminders", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_send) // Placeholder icon
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }
}
