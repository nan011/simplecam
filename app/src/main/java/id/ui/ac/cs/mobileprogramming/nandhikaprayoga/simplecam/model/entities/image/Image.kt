package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image

import android.text.format.DateUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Image(
    @ColumnInfo(name = "image_path")
    val imagePath: String,

    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    var createdAt: Long = System.currentTimeMillis(),
)

