package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.preview

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ImagePreviewActivity : AppCompatActivity() {
    companion object {
        const val ARG_IMAGE_PATH = "ARG_IMAGE_PATH"
        const val ARG_IMAGE_ID = "ARG_IMAGE_ID"
        const val RESULT_DELETE_IMAGE_ID = "RESULT_IMAGE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.setStatusBarColor(this.window, R.color.colorPrimary)
        setContentView(R.layout.activity_preview)

        setResult(Activity.RESULT_OK)
        val imagePath = intent.extras?.getString(ARG_IMAGE_PATH)

        backButton.setOnClickListener {
            super.onBackPressed()
        }

        deleteButton.setOnClickListener {
            if (imagePath != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val previewedImage = File(imagePath)
                    previewedImage.delete()
                }

                val imageID = intent.extras?.getString(ARG_IMAGE_ID)
                val returnIntent = Intent()
                returnIntent.putExtra(RESULT_DELETE_IMAGE_ID, imageID)
                setResult(Activity.RESULT_CANCELED, returnIntent)
                onBackPressed()
            }
        }

        Glide
            .with(this)
            .asBitmap()
            .load(imagePath)
            .into(imageView)
    }
}