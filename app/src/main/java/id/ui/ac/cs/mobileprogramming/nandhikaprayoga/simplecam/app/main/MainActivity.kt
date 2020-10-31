package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.camera.CameraActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.setStatusBarColor(this.window, R.color.colorPrimary)
        setContentView(R.layout.activity_main)

        val cameraIntent = Intent(this, CameraActivity::class.java)
        startActivity(cameraIntent)
    }
}