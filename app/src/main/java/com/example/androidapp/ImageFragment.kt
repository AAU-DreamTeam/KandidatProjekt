package com.example.androidapp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels

class ImageFragment : Fragment() {
    private val viewModel: ImageTextViewModel by activityViewModels()
    private lateinit var imageView : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false)
        imageView = rootView.findViewById(R.id.img_view)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.image.observe(viewLifecycleOwner, { image ->
            imageView.setImageBitmap(image)
        })
    }
}