package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.repositories.ImageRepository
import kotlinx.coroutines.*
import java.io.File

class SyncService : Service() {
    companion object {
        private const val INTERVAL_MINUTES: Long = 1
        private const val NOTIFICATION_CHANNEL_ID = "DELETION_NOTIFICATION"
    }

    private var builderNotification: NotificationCompat.Builder? = null

    private var syncJob: Job? = null
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val images = ImageRepository.get(this@SyncService).images.value
                if (images != null) {
                    for (image in images) {
                        val file = File(image.imagePath)
                        if (!file.exists()) {
                            ImageRepository.get(this@SyncService).delete(image.id)
                            withContext(Dispatchers.Main) {
                                notifyUserAboutDeletion()
                            }
                        }
                    }
                }
                delay(1000 * 60 * INTERVAL_MINUTES)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun notifyUserAboutDeletion() {
        if (builderNotification == null) {
            // Setup notification
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val descriptionText = this.getString(R.string.syncservice_channel_description)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = this.getString(R.string.syncservice_channel_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            builderNotification =
                NotificationCompat.Builder(this@SyncService, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle(this.getString(R.string.syncservice_notification_title))
                    .setContentText(descriptionText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builderNotification!!.build())
        }
    }

    override fun onDestroy() {
        syncJob?.cancel()
        super.onDestroy()
    }
}