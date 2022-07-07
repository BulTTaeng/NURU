package com.example.nuru.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.nuru.LoginActivity
import com.example.nuru.MapsActivity
import com.example.nuru.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_login2.*
import android.accounts.Account

import android.accounts.AccountManager
import android.net.Uri
import com.example.nuru.MyPageActivity
import com.example.nuru.model.SignUpInfo
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class LoginFragment : Fragment() {
    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var LoginController : NavController

    lateinit var loginActivity: LoginActivity

    val db = FirebaseFirestore.getInstance()

    lateinit var username : String
    lateinit var email: String

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
        return inflater.inflate(R.layout.fragment_login2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LoginController = login_navigation.findNavController()
        progressBar_Login.visibility = View.GONE

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(loginActivity, gso)

        //google 로그인
        btnGoogleSignIn.setOnClickListener {
            signIn()
        }

        //이메일 로그인
        btnSignIn2.setOnClickListener {
            progressBar_Login.visibility = View.VISIBLE
            var id :String = etEmail.text.toString()
            var pass : String = etPassword.text.toString()

            if(id == ""){
                Toast.makeText(loginActivity, getString(R.string.give_id), Toast.LENGTH_SHORT).show()
                progressBar_Login.visibility = View.GONE
            }
            else if(pass == ""){
                Toast.makeText(loginActivity, getString(R.string.give_password), Toast.LENGTH_SHORT).show()
                progressBar_Login.visibility = View.GONE
            }
            else{
                firebaseAuth!!.signInWithEmailAndPassword(id , pass).addOnCompleteListener(loginActivity){
                    if(it.isSuccessful){
                        val ass = Intent(context, MyPageActivity::class.java)
                        startActivity(ass)
                        //LoginController.navigate(R.id.action_loginFragment2_to_myPageActivity)
                        activity?.finish()
                    }
                    else{
                        Toast.makeText(loginActivity, getString(R.string.id_password_wrong), Toast.LENGTH_SHORT).show()
                        progressBar_Login.visibility = View.GONE
                    }
                }
            }
        }

        btnCreateAccount.setOnClickListener {
            LoginController.navigate(R.id.action_loginFragment2_to_signupFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                var account = task.getResult(ApiException::class.java)
                username = account.displayName.toString()
                email = account.email.toString()
                firebaseAuthWithGoogle(account!!)
                //activity?.finish()

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    } // onActivityResult End

    private fun checkAdminOrFarmer(){

    }

    // firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(loginActivity) { task ->
                if (task.isSuccessful) {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)

                    LoginController.navigate(LoginFragmentDirections
                        .actionLoginFragment2ToCheckTypeForGoogleFragment(SignUpInfo(firebaseAuth.currentUser?.uid.toString() , username ,
                            email, "google_login")))
                    //LoginController.navigate(R.id.action_loginFragment2_to_mapsActivity)
                    //activity?.finish()
                    //toMainActivity(firebaseAuth?.currentUser)
                } else {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
                    //Snackbar.make(login_layout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }// firebaseAuthWithGoogle END

    //TODO :: 구글 로그인 할때도 isAdmin , isFarmer 체크해야 하나
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        //activity?.finish()
    }



    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    fun writeNewUserWithTaskListeners(userId: String, name: String, email: String , type : String) {

        val Info_user = db.collection("user").document(userId)

            if(Info_user == null){
                val str = ArrayList<String>()
                str.add("zerozero")

                val data = hashMapOf(
                    "name" to name,
                    "userId" to userId,
                    "email" to email,
                    "type" to type,
                    "farmList" to str
                )

                db.collection("user").document(userId)
                    .set(data)
            }
            else{
                return
            }
            //.addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            //.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }


}