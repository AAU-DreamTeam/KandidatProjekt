package com.example.androidapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class MainActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA = 2
    }

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var imgView : ImageView
    lateinit var btnScan : Button
    lateinit var txtViewBlocks : TextView
    lateinit var txtViewLines : TextView
    lateinit var txtViewElements : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgView = findViewById(R.id.imageView)
        btnScan = findViewById(R.id.scan_button)
        txtViewBlocks = findViewById(R.id.txtViewBlocks)
        txtViewLines = findViewById(R.id.txtViewLines)
        txtViewElements = findViewById(R.id.txtViewElements)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val bundle = result.data!!.extras
                val bitmap = bundle!!.get("data") as Bitmap
                imgView.setImageBitmap(bitmap)
                runTextRecognition(bitmap)
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

    private fun runTextRecognition(image: Bitmap) {
        val image = InputImage.fromBitmap(image, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
                .addOnSuccessListener { texts ->
                    processTextRecognitionResult(texts)
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    e.printStackTrace()
                }
    }

    private fun processTextRecognitionResult(texts: Text) {
        val blocks: List<TextBlock> = texts.textBlocks
        var nBlocks = 0
        var nLines = 0
        var nElements = 0

        for (block in blocks) {
            val lines = block.lines
            nBlocks++
            for (line in lines) {
                val elements = line.elements
                nLines++
                for (element in elements) {
                    nElements++
                }
            }
        }

        txtViewBlocks.append("   $nBlocks")
        txtViewLines.append("   $nLines")
        txtViewElements.append("   $nElements")
    }
}