package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image

import androidx.room.*

@Dao
interface ImageDao {
    @Query("SELECT * FROM image")
    fun all(): List<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg image: Image)

    @Query("DELETE FROM image WHERE id = :id")
    fun delete(id: String)

    @Query("UPDATE image SET image_path = :imagePath WHERE id = :id")
    fun update(id: String, imagePath: String)
}