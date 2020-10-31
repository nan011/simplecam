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

        private const val REQUEST_PERMISSION_CODE = 101
//        private const val IMAGE_GALLERY_REQUEST_CODE = 2001
    }

    private var savedImage: File? = null

//    private var flashIsOn: Boolean = false
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
            openCamera()
        } else {
            this.let { ActivityCompat.requestPermissions(it, PERMISSIONS, REQUEST_PERMISSION_CODE) }
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

//        flashButton.setOnClickListener {
//            if (camera!!.cameraInfo.hasFlashUnit()) {
//                flashIsOn = !flashIsOn
//                camera!!.cameraControl.enableTorch(flashIsOn)
//            } else {
//                Toast.makeText(this, "Unable to use flash", Toast.LENGTH_LONG).show()
//            }
//        }
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
    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind life-cycle of camera
            cameraProvider = cameraProviderFuture.get()

            // Set camera preview
            imagePreview = Preview
                .Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            // Set image capture
            imageCapture = ImageCapture.Builder().build()

            // Select back camera
            selectedCamera = CameraSelector.DEFAULT_BACK_CAMERA

            setCameraCycles()
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Set all parts of camera in correct order of cycle
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setCameraCycles() {
        if (cameraProvider == null || selectedCamera == null || imagePreview == null || imageCapture == null) return

        try {
            cameraProvider!!.unbindAll()
            camera = cameraProvider!!.bindToLifecycle(
                this,
                selectedCamera!!,
                imagePreview,
                imageCapture,
            )
        } catch (e: Exception) {
            // All exception
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

//        // Set tap-to-focus handler
//        cameraView.afterMeasured {
//            cameraView.setOnTouchListener { _, event ->
//                return@setOnTouchListener when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
////                        println("down")
//                        true
//                    }
//                    MotionEvent.ACTION_UP -> {
////                        println("up")
//                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
//                            cameraView.width.toFloat(), cameraView.height.toFloat()
//                        )
////                        println("Width ${event.x}")
////                        println("Width ${event.y}")
//                        val autoFocusPoint = factory.createPoint(event.x, event.y)
//                        try {
//                            camera?.cameraControl?.startFocusAndMetering(
//                                FocusMeteringAction.Builder(
//                                    autoFocusPoint,
//                                    FocusMeteringAction.FLAG_AF
//                                ).apply {
//                                    println("Focus")
//                                    // focus only when the user tap the preview
//                                    disableAutoCancel()
//                                }.build()
//                            )
//                        } catch (e: CameraInfoUnavailableException) {
//                            Toast.makeText(this@CameraActivity, "cannot access camera", Toast.LENGTH_LONG).show()
//                        }
//                        true
//                    }
//                    else -> false // Unhandled event.
//                }
//            }
//        }
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
            openCamera()
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
