package com.example.nuru.view.fragment.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.R
import com.example.nuru.databinding.FragmentLoginBinding
import com.example.nuru.model.data.login.SignUpInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_login.*
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.login.UserViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.activity.OnBackPressedCallback




class LoginFragment : Fragment() {
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var LoginController : NavController

    lateinit var loginActivity: LoginActivity

    private val userViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }

    private lateinit var binding: FragmentLoginBinding
    private var backPressedTime : Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginController = login_navigation.findNavController()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    backPressedTwice()
                }
            }
        loginActivity.onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        binding.fragment = this@LoginFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar_login.visibility = View.GONE

        googleSignInClient = userViewModel.getGoogleSignInClient(loginActivity)

        btn_googleSignIn.setOnClickListener {
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progressBar_login.visibility = View.VISIBLE
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                var account = task.getResult(ApiException::class.java)
                var condition : Int = -1
                CoroutineScope(Dispatchers.Main).launch {
                    CoroutineScope(Dispatchers.IO).launch {
                        condition = userViewModel.firebaseAuthWithGoogle(account!!, loginActivity)
                    }.join()
                    when (condition) {
                        0 -> {
                            progressBar_login.visibility = View.GONE
                            val intent = Intent(loginActivity , MyPageActivity()::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                        1 -> {
                            progressBar_login.visibility = View.GONE
                            LoginController.navigate(
                                LoginFragmentDirections.actionLoginFragmentToCheckTypeForGoogleFragment(
                                    SignUpInfo(FirebaseAuth.getInstance().currentUser?.uid.toString(), account.displayName as String, account.email as String, "google_login")
                                )
                            )
                        }
                        2 -> {
                            Toast.makeText(loginActivity , "로그인에 실패하였습니다." , Toast.LENGTH_SHORT).show()
                            progressBar_login.visibility = View.GONE
                        }
                    }
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("[LoginActivity]", "Google sign in failed", e)
                btn_googleSignIn.isEnabled=true
                progressBar_login.visibility = View.GONE
            }
        }
    } // onActivityResult End

    fun btnToLoginEmail(view : View){
        LoginController.navigate(R.id.action_loginFragment_to_loginEmailFragment)
    }

    private fun signIn() {
        btn_googleSignIn.isEnabled=false
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        //activity?.finish()
    }

    fun backPressedTwice() {
        Log.d("TAG", "뒤로가기")

        // 2초내 다시 클릭하면 앱 종료
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            activity?.finish()
            return
        }

        // 처음 클릭 메시지
        Toast.makeText(loginActivity, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}