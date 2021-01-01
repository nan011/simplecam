package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        const val RESULT_IMAGE_PATH_KEY = "RESULT_IMAGE_PATH"
    }

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
            startCamera(
                selectedCamera = selectCamera(true)
            )
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
        backButton.setOnClickListener {
            this.onBackPressed()
        }

        capture.setOnClickListener {
            takePicture()
        }

        flash.setOnClickListener {
            if (camera!!.cameraInfo.hasFlashUnit()) {
                turnFlash(!flashIsOn)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.cameraactivity_error_flash),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        cameraSwitcher.setOnClickListener {
            startCamera(
                selectedCamera = selectCamera(!isFrontCamera),
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
    private fun startCamera(
        selectedCamera: CameraSelector? = null,
        imagePreview: Preview? = null,
        imageCapture: ImageCapture? = null,
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind life-cycle of camera
            cameraProvider = cameraProviderFuture.get()

            if (selectedCamera == null) {
                this@CameraActivity.selectedCamera = selectCamera(false)
            }

            if (imagePreview == null) {
                this.imagePreview = Preview
                    .Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    }
            }

            if (imageCapture == null) {
                this.imageCapture = ImageCapture.Builder().build()
            }

            try {
                cameraProvider!!.unbindAll()
                camera = cameraProvider!!.bindToLifecycle(
                    this,
                    this.selectedCamera!!,
                    this.imagePreview,
                    this.imageCapture,
                )
                camera!!.cameraControl.enableTorch(this.flashIsOn)
            } catch (e: Exception) {
                // All exception
                Toast.makeText(
                    this,
                    resources.getString(R.string.cameraactivity_error_failsetup),
                    Toast.LENGTH_LONG,
                ).show()
                e.printStackTrace()
            }


        }, ContextCompat.getMainExecutor(this))
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    /**
     * Set a current image on surface and save it to the media folder
     * The filename format: "yyyy-MM-dd HH:mm:ss.jpeg"
     *
     */
    private fun takePicture() {
        CoroutineScope(Dispatchers.IO).launch {
            // Don't take a picture if imageCapture have not been initialized
            if (imageCapture == null) return@launch

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
                ContextCompat.getMainExecutor(this@CameraActivity),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Toast.makeText(
                            this@CameraActivity,
                            resources.getString(R.string.cameraactivity_success_takepicture),
                            Toast.LENGTH_LONG
                        ).show()
                        val returnIntent = Intent()
                        returnIntent.putExtra(RESULT_IMAGE_PATH_KEY, takenImage.path)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            this@CameraActivity,
                            resources.getString(R.string.cameraactivity_error_takepicture),
                            Toast.LENGTH_LONG
                        ).show()
                        exception.printStackTrace()
                    }
                }
            )
        }
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
                resources.getString(R.string.cameraactivity_error_nopermission),
                Toast.LENGTH_LONG
            ).show()
            finish()
        } else {
            startCamera()
        }
    }
}
