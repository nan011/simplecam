package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import android.view.Window
import android.view.WindowManager
import com.google.gson.Gson
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import java.io.*


/**
 * Collection of common functions
 *
 */
class Utility {
    companion object {
        /**
         * Hide all notifications on status bar
         * It is helpful to avoid user from notifications and let them to focus on current activity
         *
         * @param window    Window of activity
         */
        fun hideStatusBar(window: Window) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        /**
         * Set status bar color as you want by passing color id through argument
         *
         * @param window    Window of activity
         * @param context   context of activity
         * @param color     Color id
         */
        fun setStatusBarColor(window: Window, color: Int) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }

        /**
         * Get available output directory from media directory
         * Use file directory if media directory doesn't exist
         *
         * @param context       Context of activity
         * @return              File of directory
         */
        fun getOutputDirectory(context: Activity): File {
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, context.getString(R.string.app_name)).apply { mkdirs() }
            }

            return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        }

        /**
         * Trim path name and return file name only
         *
         * @param path  Path name
         * @return      File name
         */
        fun getBasename(path: String): String {
            return path.substring(path.lastIndexOf(File.separator) + 1)
        }

        /**
         * Get filename from URI that has "content://" on its path
         *
         * @param context   Context of activity
         * @param uri       The targeted URI
         */
        private fun getFileNameFromUriContent(context: Context, uri: Uri): String {
            var name = ""
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                name = returnCursor.getString(nameIndex)
                returnCursor.close()
            }

            return name
        }

        /**
         * Get file from Uri instance if exist
         *
         * @param context   Context of activity
         * @param uri       The Uri
         * @return          Whether return file or not, depends on file exist or not from given uri
         */
        fun getFile(context: Context, uri: Uri): File? {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r", null)
            var file: File? = null
            parcelFileDescriptor?.let {
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                file = File(context.cacheDir, getFileNameFromUriContent(context, uri))
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(out = outputStream)
            }
            return file
        }

        /**
         * Compress image with variant quality between 0 - 100 (worst - best)
         *
         * @param path          The path of image
         * @param quality       Quality of image (0 - 100)
         * @param extension     Bitmap compress format, default is JPEG
         */
        fun compressImage(
            path: String,
            quality: Int,
            extension: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        ) {
            val file = File(path)
            if (!file.exists()) return

            val exifOrientation = ExifInterface(path).getAttribute(ExifInterface.TAG_ORIENTATION)
            var outStream: OutputStream? = null
            try {
                val imageBitmap: Bitmap? = BitmapFactory.decodeFile(file.absolutePath)
                outStream = BufferedOutputStream(FileOutputStream(file))
                imageBitmap?.compress(extension, quality, outStream)

                val currentExif = ExifInterface(path)
                currentExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation)
                currentExif.saveAttributes()
            } catch (e: IOException) {
                throw e
            } finally {
                outStream?.close()
            }
        }

        fun stringifyJSON(data: HashMap<String, Any>): String {
            return Gson().toJson(data).toString()
        }
    }
}