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
            loginType = it["type"].toString()

            if(loginType.equals("email_login")){
                firebaseAuth.signOut()
                check = true
                isSuccess = false
            }
            else if(loginType.equals("google_login")){
                isSuccess = true
            }
        }.await()
        // Firebase sign out
        googleSignInClient.signOut().addOnCompleteListener{
            if(it.isSuccessful){
                firebaseAuth.signOut()
                check = true
            }
            else{
                check = false
            }
        }.await()
        return check
    }
}