package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image.Image
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image.ImageDao

@Database(entities = [Image::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        private var db: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            if (db != null) {
                return db as AppDatabase
            }

            return kotlin.synchronized(this) {
                db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database"
                ).build()
                db!!
            }
        }
    }
}