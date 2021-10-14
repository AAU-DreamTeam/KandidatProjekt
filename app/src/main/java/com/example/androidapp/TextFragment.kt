package com.example.androidapp

import android.graphics.Bitmap
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.mlkit.vision.text.Text

class TextFragment : Fragment(R.layout.fragment_text) {
    private val viewModel: ImageTextViewModel by activityViewModels()
    private lateinit var textView : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_text, container, false)
        textView = rootView.findViewById(R.id.txt_view)
        textView.movementMethod = ScrollingMovementMethod()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.text.observe(viewLifecycleOwner, { text ->
            for (block in text.textBlocks) {
                textView.append(block.text)
            }
        })
    }

}