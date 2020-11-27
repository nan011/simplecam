package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.AppDatabase
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image.Image
import kotlinx.coroutines.*

class ImageRepository(
    private val context: Context,
) {
    var images = MutableLiveData<List<Image>>()
    companion object {
        private var instance: ImageRepository? = null

        fun get(context: Context): ImageRepository {
            if (instance != null) {
                return instance as ImageRepository
            }

            return synchronized(this) {
                this.instance = ImageRepository(context)
                instance as ImageRepository
            }
        }
    }

    init {
        getData()
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            val value = AppDatabase.get(context).imageDao().all()
            withContext(Dispatchers.Main) {
                images.value = value
            }
        }
    }

    fun delete(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.get(context).imageDao().delete(id)
            getData()
        }
    }

    fun insert(image: Image) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.get(context).imageDao().insert(image)
            getData()
        }
    }
}