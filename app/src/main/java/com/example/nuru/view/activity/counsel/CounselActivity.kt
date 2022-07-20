package com.example.nuru.view.activity.counsel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.R
import com.example.nuru.databinding.ActivityCounselBinding
import com.example.nuru.view.adapter.CounselAdapter
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.example.nuru.viewmodel.counsel.CounselViewModel

class CounselActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCounselBinding
    lateinit var counselViewModel : CounselViewModel
    private lateinit var counselAdapter : CounselAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_counsel)
        binding.activity = this@CounselActivity
        binding.widgetProgressbarInCounsel.visibility = View.GONE
        // view model 중복 선언 방지 ViewModelProvider
        counselViewModel = ViewModelProvider(this).get(CounselViewModel::class.java)

        counselAdapter = CounselAdapter(this, counselViewModel)
        binding.nameCardRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.nameCardRecyclerView.adapter = counselAdapter
        observeData()
    }

    fun btnBack(view : View) {
        // TODO : BackButton
        Log.d("CounselActivity", "Back Button이 눌렸습니다.")
    }

    fun observeData() {
        counselViewModel.fetchData().observe(this, Observer {
            binding.widgetProgressbarInCounsel.visibility = View.VISIBLE
            counselAdapter.submitList(it.map{it.copy()})
            binding.widgetProgressbarInCounsel.visibility = View.GONE
        })
    }
}