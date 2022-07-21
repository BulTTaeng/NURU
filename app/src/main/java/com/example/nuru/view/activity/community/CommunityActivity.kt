package com.example.nuru.view.activity.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nuru.R
import com.example.nuru.viewmodel.community.CommunityViewModel

class CommunityActivity : AppCompatActivity() {
    private val communityViewModel by lazy { ViewModelProvider(this).get(CommunityViewModel::class.java) }
    lateinit var communityNavController : NavController
    lateinit var navHostFragment : NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_community_container) as NavHostFragment
        communityNavController = navHostFragment.navController
    }
}