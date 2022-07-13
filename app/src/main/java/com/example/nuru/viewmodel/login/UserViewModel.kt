package com.example.nuru.viewmodel.login

import com.example.nuru.repository.user.LoginRepository
import com.example.nuru.repository.user.SignUpRepository
import com.example.nuru.view.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class UserViewModel {
    var signUpRepository: SignUpRepository = SignUpRepository()
    var loginRepository: LoginRepository = LoginRepository()

    suspend fun registerUser(email : String, pass: String, loginActivity: LoginActivity, name : String, isAdmin: Boolean, isFarmer: Boolean) : Boolean {
        return signUpRepository.registerUser(email, pass, loginActivity, name, isAdmin, isFarmer)
    }

    fun getGoogleSignInClient(loginActivity: LoginActivity) : GoogleSignInClient {
        return loginRepository.getGoogleSignInClient(loginActivity)
    }

    suspend fun firebaseAuthWithGoogle(googleSignInAccount: GoogleSignInAccount, loginActivity: LoginActivity) : Boolean{
        return loginRepository.firebaseAuthWithGoogle(googleSignInAccount, loginActivity)
    }

    suspend fun emailSignIn(id: String, pass: String, loginActivity: LoginActivity) : Boolean {
        return loginRepository.emailSignIn(id, pass, loginActivity)
    }
}