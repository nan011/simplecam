package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.camera.CameraActivity
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common.Utility
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAKE_PICTURE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.setStatusBarColor(this.window, R.color.colorPrimary)
        setContentView(R.layout.activity_main)
        fetchImages()

        openCameraButton.setOnClickListener {
            openCamera()
        }
    }

    private fun fetchImages() {
        val list1 = ArrayList<String>()
        list1.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list1.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list1.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list1.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list1.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list1.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")

        val list2 = ArrayList<String>()
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list2.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")

        val list3 = ArrayList<String>()
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list3.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")

        val list4 = ArrayList<String>()
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list4.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")

        val list5 = ArrayList<String>()
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list5.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")

        val list6 = ArrayList<String>()
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")
        list6.add("https://miro.medium.com/max/659/1*Z895h-ArPXc-eny64GENVg.jpeg")

        val names = ArrayList<String>()
        names.add("Today")
        names.add("Yesterday")
        names.add("20 November")
        names.add("1 Januari 2019")
        names.add("17 February 2007")
        names.add("30 March 2005")

        val container = ArrayList<ArrayList<String>>()
        container.add(list1)
        container.add(list2)
        container.add(list3)
        container.add(list4)
        container.add(list5)
        container.add(list6)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        imageContainer.layoutManager = linearLayoutManager
        imageContainer.adapter = ImageContainerAdapter(this, names, container)
    }

    private fun openCamera() {
        val cameraIntent = Intent(this, CameraActivity::class.java)
        startActivityForResult(cameraIntent, TAKE_PICTURE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PICTURE_REQUEST) {
            val imagePath: String = data?.getStringExtra(CameraActivity.RESULT_IMAGE_PATH_KEY) as String
        }
    }
}