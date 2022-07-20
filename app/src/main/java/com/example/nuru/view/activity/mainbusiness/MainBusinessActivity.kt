package com.example.nuru.view.activity.mainbusiness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.R
import com.example.nuru.view.adapter.MainBusinessAdapter
import com.example.nuru.viewmodel.mainbusiness.MainBusinessViewModel
import kotlinx.android.synthetic.main.fragment_main_business1.*

class MainBusinessActivity : AppCompatActivity() {

    private val mainBusinessViewModel by lazy { ViewModelProvider(this).get(MainBusinessViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_business)
    }
}