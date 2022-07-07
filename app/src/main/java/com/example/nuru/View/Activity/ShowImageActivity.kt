package com.example.nuru.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.nuru.R
import com.example.nuru.View.Adapter.ShowImageAdapter

class ShowImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)
        var viewPager = findViewById(R.id.viewPagerImage) as ViewPager2
        //img = findViewById(R.id.img_imageCommunity) as ImageView
        val Intent = intent
        var data = Intent.getStringArrayListExtra("DATA")
        viewPager.adapter = ShowImageAdapter(data!!,this)
    }


}