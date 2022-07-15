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
import androidx.navigation.NavController
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
import com.example.nuru.viewmodel.login.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var LoginController : NavController

    lateinit var loginActivity: LoginActivity

    var userViewModel : UserViewModel = UserViewModel()

    private lateinit var binding: FragmentLoginBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        LoginController = login_navigation.findNavController()
        progressBar_login.visibility = View.GONE

        googleSignInClient = userViewModel.getGoogleSignInClient(loginActivity)
        btn_googleSignIn.setOnClickListener{
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
                                LoginFragmentDirections.actionLoginFragment2ToCheckTypeForGoogleFragment(
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

    // Email SignIn
    fun btnSignIn(view:View){
        progressBar_login.visibility = View.VISIBLE
        btn_signIn.isEnabled=false
        var id :String = edt_email.text.toString()
        var pass : String = edt_password.text.toString()

        if(id == ""){
            Toast.makeText(loginActivity, getString(R.string.give_id), Toast.LENGTH_SHORT).show()
            btn_signIn.isEnabled=true
            progressBar_login.visibility = View.GONE
        }
        else if(pass == ""){
            Toast.makeText(loginActivity, getString(R.string.give_password), Toast.LENGTH_SHORT).show()
            btn_signIn.isEnabled=true
            progressBar_login.visibility = View.GONE
        }
        else {
            var loginCheck : Boolean = false
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    loginCheck = userViewModel.emailSignIn(id, pass, loginActivity)
                }.join()
                when (loginCheck) {
                    true -> {
                        progressBar_login.visibility = View.GONE
                        val ass = Intent(context, MyPageActivity::class.java)
                        startActivity(ass)
                        activity?.finish()
                    }
                    false -> {
                        progressBar_login.visibility = View.GONE
                        Toast.makeText(loginActivity, getString(R.string.id_password_wrong), Toast.LENGTH_SHORT).show()
                        btn_signIn.isEnabled=true
                    }
                }
            }
        }
    }

    fun btnCreateAccount(view: View){
        LoginController.navigate(R.id.action_loginFragment2_to_signupFragment)
    }

    private fun signIn() {
        btn_googleSignIn.isEnabled=false
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        //activity?.finish()
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}