package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.AppDatabase
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.repositories.ImageRepository
import kotlinx.coroutines.*
import java.io.File

class SyncService: Service() {
    companion object {
        private const val INTERVAL_MINUTES: Long = 1
    }

    private var syncJob: Job? = null
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                println("In loop")
                val images = ImageRepository.get(this@SyncService).images.value
                if (images != null) {
                    for (image in images) {
                        println(image)
                        val file = File(image.imagePath)
                        if (!file.exists()) {
                            ImageRepository.get(this@SyncService).delete(image.id)
                        }
                    }
                }
                delay(1000 * 60 * INTERVAL_MINUTES)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        syncJob?.cancel()
        super.onDestroy()
    }
}