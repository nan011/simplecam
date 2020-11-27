package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.camera.CameraActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.preview.ImagePreviewActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image.Image
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.receivers.NetworkChangeReceiver
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.services.SyncService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAKE_PICTURE_REQUEST = 1
        const val REQUEST_PREVIEW_IMAGE = 2
    }

    private var imageViewModel: ImageViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        this.registerReceiver(NetworkChangeReceiver(), intentFilter)

        super.onCreate(savedInstanceState)
        Utility.setStatusBarColor(this.window, R.color.colorPrimary)
        setContentView(R.layout.activity_main)

        imageViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory(
                application
            )
        ).get(ImageViewModel::class.java)

        imageViewModel!!.images.observe(this, {
            setImages()
        })

        startService(Intent(this, SyncService::class.java))

        openCameraButton.setOnClickListener {
            openCamera()
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, SyncService::class.java))
        super.onDestroy()
    }

    private fun setImages() {
        CoroutineScope(Dispatchers.Main).launch {
            if (imageViewModel == null) return@launch

            val container = ArrayList<Pair<String, ArrayList<Image>>>()
            val todayImages = imageViewModel!!.images.value!!.first
            val restImages = imageViewModel!!.images.value!!.second
            if (todayImages.second.size > 0) {
                container.add(todayImages)
            }

            if (restImages.size > 0) {
                container.addAll(restImages)
            }

            val linearLayoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            imageContainer.layoutManager = linearLayoutManager
            imageContainer.adapter = ImageContainerAdapter(this@MainActivity, container)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(this, CameraActivity::class.java)
        startActivityForResult(cameraIntent, TAKE_PICTURE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            val imagePath: String =
                data?.getStringExtra(CameraActivity.RESULT_IMAGE_PATH_KEY) as String
            imageViewModel?.addImage(imagePath)
        } else if (requestCode == REQUEST_PREVIEW_IMAGE && resultCode == RESULT_CANCELED) {
            val imageID =
                data?.getStringExtra(ImagePreviewActivity.RESULT_DELETE_IMAGE_ID) as String
            imageViewModel?.delete(imageID)
        }
    }
}