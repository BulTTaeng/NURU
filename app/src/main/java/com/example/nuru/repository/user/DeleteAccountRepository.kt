package com.example.nuru.repository.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DeleteAccountRepository {
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun deleteAccount() : Boolean {
        val fAuth: FirebaseUser = firebaseAuth.getCurrentUser()!!
        val uuid = fAuth.uid
        var check : Boolean = false
        var isSuccess : Boolean = false

        fAuth!!.delete().addOnCompleteListener{
            if(it.isSuccessful){
                FirebaseAuth.getInstance().signOut()
                isSuccess = true
            }
        }.await()
        if (isSuccess) {
            db.collection("user").document(uuid).delete().addOnCompleteListener{
                if(it.isSuccessful){
                    FirebaseAuth.getInstance().signOut()
                    check = true
                }
                else{
                    check = false
                }
            }.await()
        }
        else {
            check = false
        }
        return check
    }
}