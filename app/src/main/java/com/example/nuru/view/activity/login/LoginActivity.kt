package com.example.nuru.view.activity.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.nuru.R
import com.example.nuru.utility.GetCurrentContext

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var LoginController : NavController
    var singletonC = GetCurrentContext.getInstance()

    //private const val TAG = "GoogleActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        singletonC.setcurrentContext(this)

        LoginController = findNavController(R.id.login_navigation)
    }

    // onStart. 유저가 앱에 이미 구글 로그인을 했는지 확인
    public override fun onStart() {
        super.onStart()
    } //onStart End

    override fun onClick(p0: View?) {
    }

}