package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Use to get image either from camera or gallery
 *
 */
class CameraActivity : AppCompatActivity() {
    companion object {
        private val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val CAMERA_FLASH_IS_ON_KEY = "CAMERA_FLASH_IS_ON"
        private const val CAMERA_USES_FRONT_LENS_KEY = "CAMERA_USES_FRONT_LENS"
        private const val REQUEST_PERMISSION_CODE = 101
//        private const val IMAGE_GALLERY_REQUEST_CODE = 2001
    }

    private var savedImage: File? = null

    private var flashIsOn = false
    private var isFrontCamera = false
    private var cameraProvider: ProcessCameraProvider? = null
    private var selectedCamera: CameraSelector? = null
    private var camera: Camera? = null
    private var imagePreview: Preview? = null
    private var imageCapture: ImageCapture? = null

    /**
     * Not only create view but also set all listeners and check camera permission
     *
     * @param   savedInstanceState  Bundle of activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.hideStatusBar(window)
        Utility.setStatusBarColor(window, Color.BLACK)
        supportActionBar?.hide()
        setContentView(R.layout.activity_camera)
        setListeners()

        if (hasCameraPermission()) {
            startCamera()
        } else {
            this.let { ActivityCompat.requestPermissions(it, PERMISSIONS, REQUEST_PERMISSION_CODE) }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        flashIsOn = savedInstanceState.getBoolean(CAMERA_FLASH_IS_ON_KEY)
        isFrontCamera = savedInstanceState.getBoolean(CAMERA_USES_FRONT_LENS_KEY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CAMERA_FLASH_IS_ON_KEY, flashIsOn)
        outState.putBoolean(CAMERA_USES_FRONT_LENS_KEY, isFrontCamera)
    }

    private fun turnFlash(shouldTurnOnFlash: Boolean) {
        flashIsOn = shouldTurnOnFlash
        if (camera!!.cameraInfo.hasFlashUnit()) {
            camera!!.cameraControl.enableTorch(flashIsOn)
        } else {
            Toast.makeText(this, "Unable to use a flash", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectCamera(shouldUseFrontCamera: Boolean): CameraSelector {
        isFrontCamera = shouldUseFrontCamera
        return if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    /**
     * Set button listeners
     *
     */
    private fun setListeners() {
        capture.setOnClickListener {
            takePicture()
        }

        flash.setOnClickListener {
            if (camera!!.cameraInfo.hasFlashUnit()) {
                turnFlash(!flashIsOn)
            } else {
                Toast.makeText(this, "Unable to use flash", Toast.LENGTH_LONG).show()
            }
        }

        cameraSwitcher.setOnClickListener {
            setCameraCycles(
                selectedCamera = selectCamera(!isFrontCamera),
                imagePreview = Preview
                    .Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    },
            )
        }
    }

    /**
     * Check whether all needed permission are granted by client or not
     *
     */
    private fun hasCameraPermission(): Boolean {
        return PERMISSIONS.fold(
            true,
            { allPermissions, permission ->
                allPermissions && this.let {
                    ActivityCompat.checkSelfPermission(
                        it, permission
                    )
                } == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    /**
     * Setup camera and integrate it to the surface
     *
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind life-cycle of camera
            cameraProvider = cameraProviderFuture.get()

            setCameraCycles(
                // Select back camera
                selectedCamera = selectCamera(false),
                // Set camera preview
                imagePreview = Preview
                    .Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    },
                // Set image capture
                imageCapture = ImageCapture.Builder().build()
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setCameraCycles(
        selectedCamera: CameraSelector? = null,
        imagePreview: Preview? = null,
        imageCapture: ImageCapture? = null,
    ) {
        if (cameraProvider == null) return

        if (selectedCamera != null) {
            this.selectedCamera = selectedCamera
        }

        if (imagePreview != null) {
            this.imagePreview = imagePreview
            println("test")
        }

        if (imageCapture != null) {
            this.imageCapture = imageCapture
        }

        if (this.selectedCamera == null || this.imagePreview == null || this.imageCapture == null) {
            return
        }

        try {
            cameraProvider!!.unbindAll()
            camera = cameraProvider!!.bindToLifecycle(
                this,
                this.selectedCamera!!,
                this.imagePreview,
                this.imageCapture,
            )
        } catch (e: Exception) {
            // All exception
            Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    /**
     * Set a current image on surface and save it to the media folder
     * The filename format: "yyyy-MM-dd HH:mm:ss.jpeg"
     *
     */
    private fun takePicture() {
        // Don't take a picture if imageCapture have not been initialized
        if (imageCapture == null) return

        // Get media folder
        val mediaFolder = File(
            "${
                Utility.getOutputDirectory(
                    this@CameraActivity
                ).path
            }/images"
        )

        // Set file name
        val fileName = "${
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.US
            ).format(System.currentTimeMillis())
        }.jpg"

        // Check whether the media folder exists or not, if doesn't then create the folder
        if (!mediaFolder.exists()) {
            mediaFolder.mkdirs()
        }
        val takenImage = File(mediaFolder, fileName)

        // Set an empty file as output of image capturing
        val outputOptions = ImageCapture.OutputFileOptions.Builder(takenImage).build()

        // Create an image in given file
        imageCapture!!.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedImage = takenImage
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Failed to take a picture, try again",
                        Toast.LENGTH_LONG
                    ).show()
                    exception.printStackTrace()
                }
            }
        )
    }

    /**
     * Permission result from permission request.
     * For this case, only use to check whether the application could be able to use camera or not.
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
            Toast.makeText(
                this@CameraActivity,
                "Sorry, camera permission is needed",
                Toast.LENGTH_LONG
            ).show()
            finish()
        } else {
            startCamera()
        }
    }
}

//inline fun PreviewView.afterMeasured(crossinline block: () -> Unit) {
//    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//        override fun onGlobalLayout() {
//            if (measuredWidth > 0 && measuredHeight > 0) {
//                viewTreeObserver.removeOnGlobalLayoutListener(this)
//                block()
//            }
//        }
//    })
//}
