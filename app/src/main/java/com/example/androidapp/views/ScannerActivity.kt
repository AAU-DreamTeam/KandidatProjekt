package com.example.androidapp.views

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.views.adapters.RecyclerAdapter
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.card_layout.*



class ScannerActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager?=null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

       setupRecyclerView()

        btn_save.setOnClickListener {
           showPopupMenu()
       }

        //setupDropdownMenu()

    }


    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, btn_save)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->

            when (item.itemId) {

                R.id.action_item1 -> {
                    Toast.makeText(this, "action item2 clicked", Toast.LENGTH_SHORT).show()
                }

                R.id.action_item2 -> {
                    Toast.makeText(this, "action item2 clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.action_item3 -> {
                    Toast.makeText(this, "action item3 clicked", Toast.LENGTH_SHORT).show()
                }

                R.id.action_item4 -> {
                    Toast.makeText(this, "action item4 clicked", Toast.LENGTH_SHORT).show()
                }


            }

            true
        })

    }

    private fun setupRecyclerView(){
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerAdapter()
        recyclerView.adapter = adapter
    }

/*
    private fun setupDropdownMenu(){
        //val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val countries = resources.getStringArray(R.array.countries)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, countries)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(adapter)
    }
*/
}

