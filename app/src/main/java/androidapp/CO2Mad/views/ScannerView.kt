package androidapp.CO2Mad.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidapp.CO2Mad.R
import androidapp.CO2Mad.models.enums.COMPLETED
import androidapp.CO2Mad.viewmodels.ScannerViewModel
import androidapp.CO2Mad.views.adapters.ScannerAdapter
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.File
import java.io.IOException

class ScannerView : AppCompatActivity() {
    private val viewModel = ScannerViewModel()
    private lateinit var completedCard: CardView
    private lateinit var missingCard: CardView


    private lateinit var constraintView:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        completedCard = findViewById(R.id.completedCard)
        missingCard = findViewById(R.id.missingCard)
        constraintView= findViewById(R.id.scannerViewConstraint)

        viewModel.initiate(this)
        viewModel.loadCountries()
        viewModel.loadProducts()

        viewModel.saved.observe(this) {
            if (it) {
                finish()
            } else {
                makeToast("Kan ikke gemme før alle felter er udfyldt")
            }
        }

        launchPhotoActivity()
        setupExitButtons()
        setupRecyclerListeners()

    }
    private fun setupExitButtons(){
        btn_cancel.setOnClickListener{
            finish()
            makeToast("Dit indkøb er ikke blevet gemt")
        }

        btn_save.setOnClickListener {
            viewModel.onSave()
           makeToast("Dit indkøb er blevet gemt.")
        }

    }

    private fun makeToast(text: String ) {
        return Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }


    private fun setupRecyclerListeners(){
        linearLayout1.setOnClickListener {
            if (recyclerView.isGone) {
                closeRecyclerView(recyclerView2, btn_completed_data)
                openRecyclerView(recyclerView, btn_missing_data)
                completedToBottomConstraint()


            }else {
                closeRecyclerView(recyclerView,btn_missing_data)
                openRecyclerView(recyclerView2,btn_completed_data)
                completedToTopConstraint()

            }
        }


        linearLayout2.setOnClickListener {
            if (recyclerView2.isGone) {
                closeRecyclerView(recyclerView, btn_missing_data)
                openRecyclerView(recyclerView2, btn_completed_data)
                completedToTopConstraint()

            }else {
                closeRecyclerView(recyclerView2, btn_missing_data)
                openRecyclerView(recyclerView, btn_completed_data)
                completedToBottomConstraint()

            }
        }

    }


    private fun completedToBottomConstraint() {
        val constraintSet= ConstraintSet()

        constraintSet.clone(constraintView)
        constraintSet.clear(R.id.completedCard,ConstraintSet.TOP)
        constraintSet.connect(R.id.missingCard, ConstraintSet.BOTTOM, R.id.completedCard, ConstraintSet.TOP)

        constraintSet.applyTo(constraintView)

    }

    private fun completedToTopConstraint(){

        val constraintSet= ConstraintSet()

        constraintSet.clone(constraintView)
        constraintSet.clear(R.id.missingCard, ConstraintSet.BOTTOM)
        constraintSet.connect(R.id.completedCard,ConstraintSet.TOP,R.id.missingCard,ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintView)
    }

    private fun openRecyclerView(recyclerView: RecyclerView,imageView: ImageView) {
        recyclerView.visibility = View.VISIBLE
        imageView.setImageResource(R.drawable.ic_expand_less_black_24dp)
    }

    private fun closeRecyclerView(recyclerView: RecyclerView, imageView: ImageView) {

        recyclerView.visibility = View.GONE
        imageView.setImageResource(R.drawable.ic_expand_more_black_24dp)
    }

    private fun setupRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView2.layoutManager = LinearLayoutManager(this)

        viewModel.completedPurchases.observe(this) {
            recyclerView2.adapter = ScannerAdapter(it, viewModel.products.value!!, viewModel.countries.value!!, viewModel,this.resources,COMPLETED.COMPLETED)
        }
        viewModel.missingPurchases.observe(this) {
            recyclerView.adapter = ScannerAdapter(it, viewModel.products.value!!, viewModel.countries.value!!, viewModel,this.resources,COMPLETED.MISSING)
        }
    }

    private fun launchPhotoActivity() {

        val fileName = "image"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var imageFile : File? = null

        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
            val imageUri = FileProvider.getUriForFile(this, "androidapp.CO2Mad.fileprovider", imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.onPhotoTaken(imageFile!!.absolutePath)
                setupRecyclerView()
            }
        }

        if (intent.resolveActivity(packageManager) != null) {
            resultLauncher.launch(intent)
        } else {
            Toast.makeText(this, "No app supports this action", Toast.LENGTH_SHORT).show()
        }
    }
}



