package com.example.nuru.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.app.ActivityManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.nuru.R
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity


class MainActivity : AppCompatActivity() {
    //navigation
    lateinit var navController : NavController
    //firebase
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    var singletonC = GetCurrentContext.getInstance()

    val db = FirebaseFirestore.getInstance()

    private val RC_SIGN_IN = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        singletonC.setcurrentContext(this)
        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    private fun updateUI(user: FirebaseUser?) {
        if(user == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            //navController.navigate(R.id.action_mainFragment_to_loginActivity)
        }
        else{
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
            finish()
            //navController.navigate(R.id.action_mainFragment_to_mapFragment)
        }
    }

    fun findCurrentContext(){
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = manager.getRunningTasks(1)
        val componentName = info[0].topActivity
        val ActivityName = componentName!!.shortClassName.substring(1)
    }

    fun getNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork : NetworkInfo? = cm.activeNetworkInfo
        val isConnected : Boolean = activeNetwork?.isConnectedOrConnecting == true

        return isConnected

    }




}