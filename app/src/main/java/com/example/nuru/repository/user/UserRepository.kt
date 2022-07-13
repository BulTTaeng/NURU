package com.example.nuru.repository.user

import com.example.nuru.R
import com.example.nuru.view.activity.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class UserRepository {
    val db = FirebaseFirestore.getInstance()
    var auth = Firebase.auth

    // 회원가입
    suspend fun registerUser(email : String, pass: String, loginActivity: LoginActivity, name : String, isAdmin: Boolean, isFarmer: Boolean) : Boolean {
        return try {
                auth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(loginActivity){
                    if(it.isSuccessful){
                        registerPushToken()
                        val user = auth?.currentUser?.uid
                        writeNewUserWithTaskListeners(user.toString(), name, email , loginActivity.getString(R.string.email_login) , isAdmin , isFarmer)
                    }
                    else{
                    }
                }.await()
            true
        } catch (exception: Exception){
            false
        }
    }

    fun writeNewUserWithTaskListeners(userId: String, name: String, email: String , type : String , isAdmin : Boolean , isFarmer : Boolean) {
        var temp : ArrayList<String>
        temp = ArrayList<String>()
        val data = hashMapOf(
            "name" to name,
            "userId" to userId,
            "email" to email,
            "type" to type,
            "farmList" to temp,
            "isAdmin" to isAdmin,
            "isFarmer" to isFarmer
        )

        db.collection("user").document(userId).set(data)
    }

    fun registerPushToken() {
        //v17.0.0 이전까지는
        ////var pushToken = FirebaseInstanceId.getInstance().token
        //v17.0.1 이후부터는 onTokenRefresh()-depriciated
        //var pushToken: String? = null

        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                var pushToken = task.result

                val data = hashMapOf(
                    "pushtoken" to pushToken,
                )
                db.collection("pushtokens").document(uid!!).set(data)
            }
        }
    }
}