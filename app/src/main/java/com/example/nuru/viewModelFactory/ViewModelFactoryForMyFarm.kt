package com.example.nuru.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nuru.viewmodel.CommentsViewModel
import com.example.nuru.viewmodel.MyFarmViewModel
import com.google.firebase.firestore.DocumentReference

class viewModelFactoryForMyFarm(val Ref : DocumentReference) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyFarmViewModel::class.java)) {
            MyFarmViewModel(Ref) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}