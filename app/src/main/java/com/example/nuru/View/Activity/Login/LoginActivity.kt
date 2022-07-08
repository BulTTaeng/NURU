package com.example.nuru.View.Activity.Login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.nuru.R
import com.example.nuru.Utility.GetCurrentContext

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

    /*private var backPressedTime : Long = 0
    override fun onBackPressed() {
        Log.d("TAG", "뒤로가기")

        // 2초내 다시 클릭하면 앱 종료
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            finish()
            return
        }

        // 처음 클릭 메시지
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }*/

    override fun onBackPressed() {
    }

}