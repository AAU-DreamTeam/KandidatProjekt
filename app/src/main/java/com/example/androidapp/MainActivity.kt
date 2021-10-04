package com.example.androidapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA = 2
    }

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var imgView : ImageView
    lateinit var btnScan : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgView = findViewById(R.id.imageView)
        btnScan = findViewById(R.id.scan_button)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val bundle = result.data!!.extras
                val bitmap = bundle!!.get("data") as Bitmap
                imgView.setImageBitmap(bitmap)
            }
        }

        btnScan.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager) != null) {
                resultLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No app supports this action", Toast.LENGTH_SHORT).show()
            }
        }

    }
}