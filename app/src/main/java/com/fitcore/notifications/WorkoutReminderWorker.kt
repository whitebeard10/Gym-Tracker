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
import java.time.LocalDate

@HiltWorker
class WorkoutReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val dayOfWeek = LocalDate.now().dayOfWeek
        val workoutType = when (dayOfWeek.value) {
            1, 4 -> "Push Day — Chest & Shoulders"
            2, 5 -> "Pull Day — Back & Biceps"
            3, 6 -> "Legs Day — Quads & Hamstrings"
            else -> "Rest Day — Active Recovery"
        }

        showNotification(
            title = "Rise & Grind, Abish!",
            content = "It's $workoutType today. Let's get that workout in!"
        )
        
        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "workout_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Workout Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Placeholder icon
            .setAutoCancel(true)
            .build()

        notificationManager.notify(3, notification)
    }
}
