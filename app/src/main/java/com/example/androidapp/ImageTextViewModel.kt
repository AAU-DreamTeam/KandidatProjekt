package com.example.androidapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

class ImageTextViewModel : ViewModel(){
    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap> get() = _image

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    private val _rotation = MutableLiveData<Float>()
    val rotation: LiveData<Float> get() = _rotation

    private val imagePath = MutableLiveData<String>()

    fun fetchImageAndText() {
        _image.value = BitmapFactory.decodeFile(imagePath.value)
        _rotation.value = getCameraPhotoOrientation(imagePath.value)
        image.value?.let { runTextRecognition(it) }
    }

    fun setImagePath(imagePath : String) {
        this.imagePath.value = imagePath
    }

    private fun runTextRecognition(image: Bitmap) {
        val image = InputImage.fromBitmap(image, _rotation.value!!.toInt())

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
                .addOnSuccessListener { text ->
                    setText(text)
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    e.printStackTrace()
                }
    }

    private fun setText(text : Text) {
        _text.value = buildString {
            for (block in text.textBlocks) {
                append(block.text)
            }
        }
    }

    private fun getCameraPhotoOrientation(imagePath: String?): Float {
        var rotate = 0F
        try {
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270F
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180F
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90F
            }
            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }
}