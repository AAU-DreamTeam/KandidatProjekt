package com.example.androidapp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class ImageFragment : Fragment() {
    private lateinit var imageView : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false)
        imageView = rootView.findViewById(R.id.img_view)

        return rootView
    }

    fun setImage(bitmap : Bitmap) = imageView.setImageBitmap(bitmap)
}