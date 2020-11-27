package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image.Image
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.repositories.ImageRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ImageViewModel(val app: Application) : AndroidViewModel(app) {
    companion object {
        private const val ACCESS_DATA_MAX_ITERATION = 10
    }

    enum class DateType(val value: String) {
        TODAY("Today"),
        YESTERDAY("Yesterday"),
        OTHER("Other"),
    }

    val images: LiveData<Pair<Pair<String, ArrayList<Image>>, ArrayList<Pair<String, ArrayList<Image>>>>> =
        Transformations.map(ImageRepository.get(app).images) { groupImages(it as ArrayList<Image>) }


    private fun categorizeDate(timeInMilliseconds: Long): DateType {
        if (DateUtils.isToday(timeInMilliseconds)) {
            return DateType.TODAY
        } else if (DateUtils.isToday(timeInMilliseconds + DateUtils.DAY_IN_MILLIS)) {
            return DateType.YESTERDAY
        }

        return DateType.OTHER
    }

    private fun groupImages(images: ArrayList<Image>): Pair<Pair<String, ArrayList<Image>>, ArrayList<Pair<String, ArrayList<Image>>>> {
        val todayImages = Pair(
            app.resources.getString(R.string.mainactivity_date_today),
            ArrayList<Image>(),
        )
        val restImages = ArrayList<Pair<String, ArrayList<Image>>>()

        val dateFormatter = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.US,
        )

        val groups: Map<String, List<Image>> = images.groupBy {
            dateFormatter.format(it.createdAt)
        }

        for ((_, group) in groups) {
            val imagesInArrayList = group.toMutableList() as ArrayList<Image>
            imagesInArrayList.reverse()
            val dateCategory = categorizeDate(group[0].createdAt)
            if (dateCategory == DateType.TODAY) {
                todayImages.second.addAll(imagesInArrayList)
            } else if (dateCategory == DateType.YESTERDAY || dateCategory == DateType.OTHER) {
                var title: String = dateFormatter.format(group[0].createdAt)
                if (dateCategory == DateType.YESTERDAY) {
                    title = app.resources.getString(R.string.mainactivity_date_yesterday)
                }

                restImages.add(Pair(title, imagesInArrayList))
            }
        }
        return Pair(todayImages, restImages)
    }

    fun addImage(imagePath: String) {
        val image = Image(imagePath)
        ImageRepository.get(app).insert(image)
    }

    fun delete(id: String) {
        ImageRepository.get(app).delete(id)
    }
}