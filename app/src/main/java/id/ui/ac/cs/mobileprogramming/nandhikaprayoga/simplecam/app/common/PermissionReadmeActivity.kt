package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import kotlinx.android.synthetic.main.activity_permission_readme.*

class PermissionReadmeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_readme)

        exitButton.setOnClickListener {
            finish()
        }
    }
}