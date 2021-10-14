package com.example.androidapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import androidx.fragment.app.activityViewModels


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
    private lateinit var imagePath : String
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
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                val rotation = getCameraPhotoOrientation(imagePath)
                viewModel.selectImage(bitmap)
                //imgView.setImageBitmap(bitmap)
                //imgView.rotation = rotation.toFloat()
                runTextRecognition(bitmap)
            }
        }

        scanButton.setOnClickListener{
            val fileName = "image"
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            var imageFile : File

            try {
                imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
                imagePath = imageFile.absolutePath
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

    }

    private fun runTextRecognition(image: Bitmap) {
        val image = InputImage.fromBitmap(image, 0)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
                .addOnSuccessListener { text ->
                    viewModel.selectText(text)
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    e.printStackTrace()
                }
    }

    private fun getCameraPhotoOrientation(imagePath: String): Int {
        var rotate = 0
        try {
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }
}
