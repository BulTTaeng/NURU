package com.example.nuru.viewmodel.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.nuru.repository.user.DeleteAccountRepository
import com.example.nuru.repository.user.LoginRepository
import com.example.nuru.repository.user.SignOutRepository
import com.example.nuru.repository.user.SignUpRepository
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.fragment_check_type_for_google.*

class UserViewModel : ViewModel(){
    var signUpRepository: SignUpRepository = SignUpRepository()
    var loginRepository: LoginRepository = LoginRepository()
    var signOutRepository: SignOutRepository = SignOutRepository()
    var deleteAccountRepository: DeleteAccountRepository = DeleteAccountRepository()

    suspend fun registerUser(email : String, pass: String, loginActivity: LoginActivity, name : String, isAdmin: Boolean, isFarmer: Boolean , isMember : Boolean) : Boolean {
        return signUpRepository.registerUser(email, pass, loginActivity, name, isAdmin, isFarmer,isMember)
    }

    fun getGoogleSignInClient(activity: Activity) : GoogleSignInClient {
        return loginRepository.getGoogleSignInClient(activity)
    }

    suspend fun firebaseAuthWithGoogle(googleSignInAccount: GoogleSignInAccount, loginActivity: LoginActivity) : Int {
        return loginRepository.firebaseAuthWithGoogle(googleSignInAccount, loginActivity)
    }

    suspend fun emailSignIn(id: String, pass: String, loginActivity: LoginActivity) : Boolean {
        return loginRepository.emailSignIn(id, pass, loginActivity)
    }

    suspend fun signOut(googleSignInClient: GoogleSignInClient) : Boolean {
        return signOutRepository.signOut(googleSignInClient)
    }

    suspend fun deleteAccount() : Boolean {
        return deleteAccountRepository.deleteAccount()
    }

    suspend fun googleSignUp(userId: String, name: String, email: String, type : String, isFarmer : Boolean, isAdmin : Boolean) : Boolean {
        return signUpRepository.googleSignUp(userId, name , email , type , isFarmer, isAdmin)
    }
}