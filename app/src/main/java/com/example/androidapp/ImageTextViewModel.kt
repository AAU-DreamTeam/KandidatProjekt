package com.example.androidapp

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text

class ImageTextViewModel : ViewModel(){
    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap> get() = _image

    private val _text = MutableLiveData<Text>()
    val text: LiveData<Text> get() = _text

    fun selectImage(image : Bitmap) {
        _image.value = image
    }

    fun selectText(text : Text) {
        _text.value = text
    }
}