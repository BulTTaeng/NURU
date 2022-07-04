package com.example.nuru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.model.Farm
import com.example.nuru.utility.GetCurrentContext


class MarkerListActivity : AppCompatActivity() {
    //TODO : Make marker for farms

    var singletonC = GetCurrentContext.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_list)
        singletonC.setcurrentContext(this)

        val Intent = intent
        var user_farm_list : Array<String>? = Intent.getStringArrayExtra("farmlist")

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<Farm>()

        // This loop will create 20 Views containing
        // the image with the count of view
        /*for (i in 1..20) {
            data.add(Farm(R.drawable.nuru1 , "Aaa", "Aa"))
        }*/

        // This will pass the ArrayList to our Adapter
        //val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        //recyclerview.adapter = adapter
    }
}