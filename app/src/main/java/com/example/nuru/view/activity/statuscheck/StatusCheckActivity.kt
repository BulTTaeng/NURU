package com.example.nuru.view.activity.statuscheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nuru.R

class StatusCheckActivity : AppCompatActivity() {

    lateinit var statusCheckController : NavController
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_check)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_status_check_container) as NavHostFragment
        statusCheckController = navHostFragment.navController
    }
}