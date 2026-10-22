package com.personal.freshkeep.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class ExpiryNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val items = database.itemDao().getAllItemsSortedByExpiry().first()
        val currentTime = System.currentTimeMillis()
        
        val criticalCount = items.count { 
            val daysLeft = TimeUnit.MILLISECONDS.toDays(it.expiryDateEpoch - currentTime)
            daysLeft in 0..3 
        }

        if (criticalCount > 0) {
            triggerSystemNotification(criticalCount)
        }
        return Result.success()
    }

    private fun triggerSystemNotification(count: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "freshkeep_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Expiry Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("FreshKeep Expiry Warning")
            .setContentText("You have $count items expiring within the next 3 days!")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(1001, notification)
    }
}
