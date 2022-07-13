package com.example.nuru.viewmodel.login

import com.example.nuru.repository.user.UserRepository
import com.example.nuru.view.activity.login.LoginActivity

class UserViewModel {
    var userRepository: UserRepository = UserRepository()

    suspend fun registerUser(email : String, pass: String, loginActivity: LoginActivity, name : String, isAdmin: Boolean, isFarmer: Boolean) : Boolean {
        return userRepository.registerUser(email, pass, loginActivity, name, isAdmin, isFarmer)
    }
}