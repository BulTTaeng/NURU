package com.example.nuru.view.activity.counsel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.databinding.ActivityCounselBinding

class CounselActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCounselBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_counsel)
        binding.activity = this@CounselActivity
    }

    fun btnBack(view : View) {
        // TODO : BackButton
        Log.d("CounselActivity", "Back Button이 눌렸습니다.")
    }
}