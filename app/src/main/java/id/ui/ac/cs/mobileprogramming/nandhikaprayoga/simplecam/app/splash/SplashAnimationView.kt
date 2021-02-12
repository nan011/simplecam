package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.splash

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class SplashAnimationView(
    context: Context,
    attrs: AttributeSet?,
): GLSurfaceView(context, attrs) {
    private val subscribers = HashMap<String, () -> Unit>()
    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        setRenderer(SplashAnimationRenderer())

        Timer().schedule(2000) {
            for ((_, callback) in subscribers) {
                callback()
            }
        }
    }

    fun notifyOnFinish(callback: () -> Unit): String {
        val id = UUID.randomUUID().toString()
        subscribers[id] = callback
        return id
    }

    fun removeNotification(id: String) {
        this.subscribers.remove(id)
    }
}