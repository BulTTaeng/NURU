package com.example.nuru.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.google.firebase.firestore.DocumentReference

class ViewModelFactoryForMyFarm(val Ref : DocumentReference) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyFarmViewModel::class.java)) {
            MyFarmViewModel(Ref) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}