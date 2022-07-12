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
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.R
import com.example.nuru.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.model.data.login.SignUpInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_login.*
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

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(loginActivity, gso)

        btn_googleSignIn.setOnClickListener{
            signIn()
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

    fun btnSignIn( view:View){
        progressBar_login.visibility = View.VISIBLE
        var id :String = edt_email.text.toString()
        var pass : String = edt_password.text.toString()

        if(id == ""){
            Toast.makeText(loginActivity, getString(R.string.give_id), Toast.LENGTH_SHORT).show()
            progressBar_login.visibility = View.GONE
        }
        else if(pass == ""){
            Toast.makeText(loginActivity, getString(R.string.give_password), Toast.LENGTH_SHORT).show()
            progressBar_login.visibility = View.GONE
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
                    progressBar_login.visibility = View.GONE
                }
            }
        }
    }

    fun btnCreateAccount(view: View){
        LoginController.navigate(R.id.action_loginFragment2_to_signupFragment)
    }

    // firebaseAuthWithGoogle
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
    //TODO:: 여기서 한번 firebase에 쓰고 갈까?
        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(loginActivity) { task ->
                if (task.isSuccessful) {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)

                    db.collection("user").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                        val intent = Intent(loginActivity , MyPageActivity()::class.java)
                        startActivity(intent)
                    }.addOnFailureListener{
                        LoginController.navigate(
                            com.example.nuru.view.fragment.login.LoginFragmentDirections.actionLoginFragment2ToCheckTypeForGoogleFragment(
                                SignUpInfo(
                                    firebaseAuth.currentUser?.uid.toString(), username,
                                    email, "google_login"
                                )
                            )
                        )
                    }

                    //LoginController.navigate(R.id.action_loginFragment2_to_mapsActivity)
                    //activity?.finish()
                    //toMainActivity(firebaseAuth?.currentUser)
                } else {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
                    //Snackbar.make(login_layout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }// firebaseAuthWithGoogle END

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