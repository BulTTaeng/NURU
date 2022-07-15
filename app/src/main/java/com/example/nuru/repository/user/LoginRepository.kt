package com.example.nuru.repository.user

import android.app.Activity
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

    fun getGoogleSignInClient(activity : Activity) : GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(activity, gso)
    }

    // firebaseAuthWithGoogle
    // 0 : 원래 로그인 된 계정, 1 : 처음 구글 로그인 시도, 2 : 구글 로그인 실패
    suspend fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, loginActivity: LoginActivity) : Int {
        val username: String = acct.displayName.toString()
        val email: String = acct.email.toString()
        var isSuccess : Boolean = false
        var result : Int = -1
        //TODO:: 여기서 한번 firebase에 쓰고 갈까?
        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(loginActivity) { task ->
            if (task.isSuccessful) {
                Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)
                isSuccess = true
            } else {
                Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
                result=2
            }
        }.await()
        when (isSuccess) {
            true -> {
                db.collection("user").document(firebaseAuth.currentUser!!.uid).get().addOnCompleteListener { task->
                    if (task.result["name"] as String? == null) {
                        result = 1
                    } else {
                        result = 0
                    }
                }.await()
            }
        }
        return result
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