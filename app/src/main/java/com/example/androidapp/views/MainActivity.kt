package com.example.androidapp.views

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.androidapp.ImageFragment
import com.example.androidapp.ImageTextViewModel
import com.example.androidapp.R
import com.example.androidapp.TextFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA = 2
    }

    private val viewModel: ImageTextViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var fragmentFL: FrameLayout
    private lateinit var textFragment: TextFragment
    private lateinit var imageFragment: ImageFragment
    private lateinit var toggleButton : MaterialButtonToggleGroup
    private lateinit var scanButton : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentFL = findViewById(R.id.fragment_fl)
        scanButton = findViewById(R.id.btn_scan)
        toggleButton = findViewById(R.id.toggleButton)
        textFragment = TextFragment()
        imageFragment = ImageFragment()

        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    R.id.btn_txt -> supportFragmentManager.beginTransaction().replace(fragmentFL.id, textFragment).commit()
                    R.id.btn_img -> supportFragmentManager.beginTransaction().replace(fragmentFL.id, imageFragment).commit()
                }
            } else if (toggleButton.checkedButtonId == View.NO_ID) {
                toggleButton.check(checkedId)
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.fetchImageAndText()
            }
        }

        scanButton.setOnClickListener{
            val fileName = "image"
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            var imageFile : File

            try {
                imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
                viewModel.setImagePath(imageFile.absolutePath)
                val imageUri = FileProvider.getUriForFile(this, "com.example.androidapp.fileprovider", imageFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (intent.resolveActivity(packageManager) != null) {
                resultLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No app supports this action", Toast.LENGTH_SHORT).show()
            }
        }

        supportFragmentManager.beginTransaction().replace(fragmentFL.id, imageFragment).commit()
    }
}
