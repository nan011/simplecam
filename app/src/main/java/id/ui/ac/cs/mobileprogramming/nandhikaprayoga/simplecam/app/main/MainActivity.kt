package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Utility.setStatusBarColor(this.window, this@MainActivity, R.color.colorPrimary)
        setContentView(R.layout.activity_main)
    }
}