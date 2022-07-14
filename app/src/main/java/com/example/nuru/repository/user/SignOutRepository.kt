package com.example.nuru.repository.user

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SignOutRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var loginType : String = ""

    suspend fun signOut(googleSignInClient: GoogleSignInClient) : Boolean {
        var check : Boolean = false
        var isSuccess : Boolean = false
        db.collection("user").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
            Log.d("[Repo SingOut]", "부모 addOnSuccessListener 안")
            loginType = it["type"].toString()
            Log.d("[Repo SingOut]", "loginType 지정 -> $loginType")

            if(loginType.equals("email_login")){
                Log.d("[Repo SingOut]", "loginType이 email_login이라면")
                firebaseAuth.signOut()
                Log.d("[Repo SingOut]", "firebaseAuth.singOut 완료!")
                check = true
                isSuccess = false
            }
            else if(loginType.equals("google_login")){
                Log.d("[Repo SingOut]", "loginType이 google_login이라면")
                isSuccess = true
                // Firebase sign out
                googleSignInClient.signOut().addOnCompleteListener{
                    Log.d("[Repo SingOut]", "자식 addOnCompleteListner 시작")
                    if(it.isSuccessful){
                        Log.d("[Repo SingOut]", "구글 signOut안에서 firbaseAuth signOut 시작")
                        firebaseAuth.signOut()
                        Log.d("[Repo SingOut]", "구글 signOut안에서 firbaseAuth signOut 끝")
                        Log.d("[Repo SingOut]", "자식 addOnCompleteListner 시작")
                        check = true
                    }
                    else{
                        check = false
                    }
                    Log.d("[Repo SingOut]", "자식 addOnCompleteListner 끝")
                }
            }
            Log.d("[Repo SingOut]", "부모 addOnCompleteListner 끝")
        }.await()
        return check
    }
}