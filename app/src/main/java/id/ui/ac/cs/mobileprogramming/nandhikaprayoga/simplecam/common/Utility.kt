package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Collection of common functions
 *
 */
class Utility {
    companion object {
        init {
            System.loadLibrary("native-lib")
        }

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

        fun stringifyJSON(data: HashMap<String, Any>): String {
            return Gson().toJson(data).toString()
        }

        /**
         * Parse JSON into generic object
         *
         * @param json  Serialized JSON string
         * @return      A list of key-value, represented by HashMap
         */
        fun parseJSON(json: String?): HashMap<String, Any> {
            val `object` = JsonParser.parseString(json) as JsonObject
            val set: Set<Map.Entry<String, JsonElement>> =
                `object`.entrySet()
            val iterator: Iterator<Map.Entry<String, JsonElement>> =
                set.iterator()
            val map = HashMap<String, Any>()
            while (iterator.hasNext()) {
                val entry: Map.Entry<String, JsonElement> =
                    iterator.next()
                val key = entry.key
                val value: JsonElement = entry.value
                if (!value.isJsonPrimitive) {
                    if (value.isJsonObject) {
                        map[key] = parseJSON(value.toString())
                    } else if (value.isJsonArray) {
                        val valueIsObject: Boolean = value.toString().contains(":")

                        map[key] = if (valueIsObject) {
                            val list: MutableList<HashMap<String, Any>> = ArrayList()
                            for (element in value.asJsonArray) {
                                list.add(parseJSON(element.toString()))
                            }
                            list
                        } else {
                            val list: MutableList<String> = ArrayList()
                            for (element in value.asJsonArray) {
                                list.add(element.toString())
                            }
                            list
                        }
                    }
                } else {
                    map[key] = value.asString
                }
            }
            return map
        }

        /**
         * Check whether all needed permission are granted by client or not
         *
         */
        fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
            return permissions.fold(
                true,
                { allPermissions, permission ->
                    allPermissions && this.let {
                        ActivityCompat.checkSelfPermission(
                            context, permission
                        )
                    } == PackageManager.PERMISSION_GRANTED
                }
            )
        }

        fun saveImage(context: Context, url: String, onFinish: (File?) -> Unit) {
            println("url: $url")
            CoroutineScope(Dispatchers.Main).launch {
                Picasso.with(context).load(url)
                    .into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                            println("Begin to save")
                            try {
                                val tempDirectory = File(
                                    "${context.cacheDir}/temp"
                                )

                                if (!tempDirectory.exists()) {
                                    tempDirectory.mkdirs()
                                }
                                val fileUri =
                                    tempDirectory.absolutePath + File.separator + System.currentTimeMillis()
                                        .toString() + "." + getExtensionName(url)
                                println("ready to download")
                                val outputStream = FileOutputStream(fileUri)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                println("Has compressed")
                                outputStream.flush()
                                outputStream.close()

                                println("Done")
                                onFinish(File(fileUri))
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            Toast.makeText(
                                context,
                                "Image Downloaded",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onBitmapFailed(errorDrawable: Drawable?) {
                            onFinish(null)
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            onFinish(null)
                        }
                    })
            }
        }

        fun sort(list: Array<Double>): Array<Int> {
            val temp: IntArray = cppSort(list.toDoubleArray())
            return Array(temp.size) { temp[it] }
        }

        /**
         * Sort in native
         *
         * @param list  List of values
         * @return      List of index as result of sorting (ascending order)
         */
        private external fun cppSort(list: DoubleArray): IntArray

        fun getExtensionName(filename: String): String = filename.substringAfterLast('.', "")
    }
}
