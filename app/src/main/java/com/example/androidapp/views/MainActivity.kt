package com.example.androidapp.views

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.androidapp.R
import com.example.androidapp.views.adapters.MainAdapter
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: MainAdapter

    companion object {
        var isStarted = false
        var screenRecorder: ScreenRecorder? = null
        //val ORIENTATION = SparseIntArray()
        //private lateinit var resultLauncher: ActivityResultLauncher<Intent>
        //private var mediaProjection: MediaProjection? = null
        //private var mediaProjectionManager: MediaProjectionManager? = null
        //private var virtualDisplay: VirtualDisplay? = null
        private const val PERMISSION_REQUEST_ID = 10

        /*init {
            ORIENTATION.append(Surface.ROTATION_0, 90)
            ORIENTATION.append(Surface.ROTATION_90, 0)
            ORIENTATION.append(Surface.ROTATION_180, 270)
            ORIENTATION.append(Surface.ROTATION_270, 180)
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setUpTabs()
        setUpViewPager()

        //mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        /*resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                mediaProjection = mediaProjectionManager!!.getMediaProjection(
                    it.resultCode,
                    it.data!!
                )
                shareScreen()
            } else {
                finish()
            }
        }*/

        requestPermissions()

    }

    fun requestPermissions() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_ID
            )
        } else {
            onScreenShare()
        }
    }

    private fun onScreenShare() {
        /*if (mediaProjection == null) {
            resultLauncher.launch(mediaProjectionManager!!.createScreenCaptureIntent())
        } else {*/
            shareScreen()
        //}
    }

    private fun shareScreen() {
        screenRecorder = ScreenRecorder(this)
        //virtualDisplay = createVirtualDisplay()
        screenRecorder?.start()
        Toast.makeText(this, "Skærm og lyd optages\n", Toast.LENGTH_SHORT).show()
    }

    private fun stopScreenSharing(){
        screenRecorder?.stop()
        screenRecorder?.reset()
        //virtualDisplay?.release()
        //mediaProjection?.stop()
        //mediaProjection = null
    }

    private fun hasPermissions() : Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ID && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            onScreenShare()
        } else {
            finish()
        }
    }

    /*private fun createVirtualDisplay(): VirtualDisplay? {
        return mediaProjection!!.createVirtualDisplay(
            "Virtual display",
            screenRecorder!!.metrics.widthPixels,
            screenRecorder!!.metrics.heightPixels,
            screenRecorder!!.metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            screenRecorder!!.mediaRecorder.surface,
            null,
            null
        )
    }*/

    private fun setUpTabs(){
        tabLayout.addTab(tabLayout.newTab().setText("FORBRUG"))
        tabLayout.addTab(tabLayout.newTab().setText("VARER"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun setUpViewPager(){
        viewPagerAdapter = MainAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    override fun onStart() {
        isStarted = true
        super.onStart()
        Log.i("-----------", "onStart: MainActivity")
    }

    override fun onStop() {
        isStarted = false
        super.onStop()
        Log.i("-----------", "onStop: MainActivity")
    }

    override fun onDestroy() {
        stopScreenSharing()
        super.onDestroy()
    }
}
