package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.camera.CameraActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.common.PermissionReadmeActivity
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
        private const val REQUEST_PERMISSION_CODE = 101

        private val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private var imageViewModel: ImageViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!Utility.hasPermissions(this, PERMISSIONS)) {
            this.let { ActivityCompat.requestPermissions(it,
                PERMISSIONS,
                REQUEST_PERMISSION_CODE,
            ) }
        }

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

    /**
     * Permission result from permission request.
     *
     * @param requestCode   Request code
     * @param permissions   List of permission
     * @param grantResults  List of granted permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            val permissionReadmeActivity = Intent(this, PermissionReadmeActivity::class.java)
            startActivity(permissionReadmeActivity)
            finish()
        }
    }
}