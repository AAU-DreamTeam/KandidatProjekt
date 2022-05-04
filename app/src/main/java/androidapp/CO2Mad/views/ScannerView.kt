package androidapp.CO2Mad.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidapp.CO2Mad.R
import androidapp.CO2Mad.tools.enums.Completed
import androidapp.CO2Mad.viewmodels.ScannerViewModel
import androidapp.CO2Mad.views.adapters.ScannerAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ScannerView : AppCompatActivity() {
    private val viewModel = ScannerViewModel()
    private lateinit var constraintView:ConstraintLayout
    var imageFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        constraintView= findViewById(R.id.scannerViewConstraint)

        viewModel.initiate(this)
        viewModel.loadCountries()
        viewModel.loadProducts()

        viewModel.saved.observe(this) {
            if (it) {
                val resultIntent = Intent()

                if (viewModel.completedPurchases.value!!.isNotEmpty() || viewModel.missingPurchases.value!!.isNotEmpty()) {
                    MediaStore.Images.Media.insertImage(contentResolver, imageFile!!.absolutePath, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time), null)
                    resultIntent.putExtra("reloadData", true)
                } else {
                    resultIntent.putExtra("reloadData", false)
                }

                makeToast("Dit indkøb er blevet gemt.")
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                makeToast("Kan ikke gemme før alle felter er udfyldt.")
            }
        }

        launchPhotoActivity()
        setupExitButtons()
        setupRecyclerListeners()

    }
    private fun setupExitButtons(){
        btn_cancel.setOnClickListener{
            AlertDialog.Builder(this)
                    .setTitle("Gå tilbage")
                    .setMessage("Er du sikker på at du ville gå tilbage uden at gemme?")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener{dialog, which ->
                        val resultIntent = Intent()
                        resultIntent.putExtra("reloadData", false)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                        makeToast("Dit indkøb er ikke blevet gemt")
                    })
                    .setNegativeButton(android.R.string.no,null)
                    .create()
                    .show()
        }

        btn_save.setOnClickListener {


            AlertDialog.Builder(this)
                    .setTitle("Gem")
                    .setMessage("Er du sikker på at du ville gemme?")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener{dialog, which ->
                        viewModel.onSave()
                    })
                    .setNegativeButton(android.R.string.no,null)
                    .create()
                    .show()

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
                closeRecyclerView(recyclerView2, btn_completed_data)
                openRecyclerView(recyclerView, btn_missing_data)
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
            recyclerView2.adapter = ScannerAdapter(it, viewModel.products.value!!, viewModel.countries.value!!, viewModel,this.resources,Completed.COMPLETED)
        }
        viewModel.missingPurchases.observe(this) {
            recyclerView.adapter = ScannerAdapter(it, viewModel.products.value!!, viewModel.countries.value!!, viewModel,this.resources,Completed.MISSING)
        }
    }

    private fun launchPhotoActivity() {
        val fileName = "image"
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try {
            imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
            val imageUri = FileProvider.getUriForFile(this, "androidapp.CO2Mad.fileprovider", imageFile!!)
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



