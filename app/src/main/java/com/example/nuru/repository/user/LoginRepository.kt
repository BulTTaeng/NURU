package com.example.nuru.repository.user

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.nuru.R
import com.example.nuru.model.data.login.SignUpInfo
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun getGoogleSignInClient(loginActivity : LoginActivity) : GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(loginActivity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(loginActivity, gso)
    }

    // Overrode
    fun getGoogleSignInClient(myPageActivity: MyPageActivity) : GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(myPageActivity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(myPageActivity, gso)
    }

    // firebaseAuthWithGoogle
    suspend fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, loginActivity: LoginActivity) : Boolean {
        val username: String = acct.displayName.toString()
        val email: String = acct.email.toString()
        var case : Boolean = false
        //TODO:: 여기서 한번 firebase에 쓰고 갈까?
        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(loginActivity) { task ->
                if (task.isSuccessful) {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)

                    db.collection("user").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                        case=true
                        //val intent = Intent(loginActivity , MyPageActivity()::class.java)
                        //startActivity(intent)
                    }.addOnFailureListener{
                        case=false
                        /*LoginController.navigate(
                            com.example.nuru.view.fragment.login.LoginFragmentDirections.actionLoginFragment2ToCheckTypeForGoogleFragment(
                                SignUpInfo(
                                    firebaseAuth.currentUser?.uid.toString(), username,
                                    email, "google_login"
                                )
                            )
                        )*/

                    }

                    //LoginController.navigate(R.id.action_loginFragment2_to_mapsActivity)
                    //activity?.finish()
                    //toMainActivity(firebaseAuth?.currentUser)
                } else {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
                    case = false
                    //Snackbar.make(login_layout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }.await()
        return case
    }

    // Sign In With Email and Password
    suspend fun emailSignIn(id: String, pass: String, loginActivity: LoginActivity) : Boolean {
        try {
            var check : Boolean = false
            firebaseAuth!!.signInWithEmailAndPassword(id , pass).addOnCompleteListener(loginActivity){
                check = it.isSuccessful
            }.await()
            return check
        } catch (exception : Exception) {
            Log.w("[EmailSignIn]", "Email Sign In Exception!")
            return false
        }
    }
}