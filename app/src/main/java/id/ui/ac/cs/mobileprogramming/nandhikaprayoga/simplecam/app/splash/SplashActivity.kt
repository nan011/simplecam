package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main.MainActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity: AppCompatActivity() {
    private var subscriberId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.hideStatusBar(this.window)
        setContentView(R.layout.activity_splash)
        subscriberId = splashView.notifyOnFinish {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }
    }

    override fun onResume() {
        splashView.onResume()
        super.onResume()
    }

    override fun onPause() {
        splashView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        splashView.removeNotification(subscriberId)
        super.onDestroy()
    }
}