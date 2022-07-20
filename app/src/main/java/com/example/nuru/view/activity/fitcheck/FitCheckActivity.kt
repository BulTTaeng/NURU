package com.example.nuru.view.activity.fitcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nuru.R
import com.example.nuru.viewmodel.mainbusiness.MainBusinessViewModel

class FitCheckActivity : AppCompatActivity() {

    lateinit var fitController : NavController
    lateinit var navHostFragment: NavHostFragment
    private val fitCheckViewModel by lazy { ViewModelProvider(this).get(MainBusinessViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firt_check)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fit_check_container) as NavHostFragment
        fitController = navHostFragment.navController
    }
}