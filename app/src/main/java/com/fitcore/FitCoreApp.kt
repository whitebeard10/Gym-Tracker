package com.fitcore

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fitcore.notifications.MealReminderWorker
import com.fitcore.notifications.WaterReminderWorker
import com.fitcore.notifications.WorkoutReminderWorker
import java.util.concurrent.TimeUnit

/**
 * Main Application class for FitCore.
 * Enables Hilt for Dependency Injection and configures WorkManager for background tasks.
 */
@HiltAndroidApp
class FitCoreApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleNotifications()
    }

    private fun scheduleNotifications() {
        val workManager = WorkManager.getInstance(this)

        // 1. Water Nudge every 1.5 hours (10am - 11pm logic is inside the worker)
        val waterRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(90, TimeUnit.MINUTES)
            .addTag("water_nudge")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "WaterReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            waterRequest
        )

        // 2. Daily Workout Reminder
        val workoutRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(24, TimeUnit.HOURS)
            .addTag("workout_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "WorkoutReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workoutRequest
        )

        // 3. Meal Reminders (Generic check every 4 hours)
        val mealRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(4, TimeUnit.HOURS)
            .addTag("meal_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "MealReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            mealRequest
        )
    }
}
