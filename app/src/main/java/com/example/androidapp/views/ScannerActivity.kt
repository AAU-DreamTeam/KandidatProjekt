package com.example.androidapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.viewmodels.ScannerViewModel
import com.example.androidapp.views.adapters.ScannerAdapter
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.File
import java.io.IOException
import java.lang.Exception


class ScannerActivity : AppCompatActivity() {
    private val viewModel = ScannerViewModel()
    private var layoutManager: RecyclerView.LayoutManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        viewModel.loadCountries(this)
        viewModel.loadProducts(this)

        launchPhotoActivity()

        btn_cancel.setOnClickListener{
            finish()
        }

        btn_save.setOnClickListener {
            //viewModel.onSave(this)
            finish()
        }
    }

    private fun setupRecyclerView(){
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        viewModel.purchases.observe(this) {
            recyclerView.adapter = ScannerAdapter(it, viewModel.products.value!!, viewModel.countries.value!!, viewModel)
        }
    }

    private fun launchPhotoActivity() {
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.runTextRecognition(this)
                setupRecyclerView()
            }
        }

        val fileName = "image"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile : File

        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
            viewModel.imagePath = imageFile.absolutePath
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

