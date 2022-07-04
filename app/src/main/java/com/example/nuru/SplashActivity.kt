package com.example.nuru

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.nuru.observer.NetworkConnection
import com.example.nuru.utility.GetCurrentContext

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 1000 // 2 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }


}