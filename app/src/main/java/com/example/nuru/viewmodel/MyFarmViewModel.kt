package com.example.nuru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.Comments
import com.example.nuru.model.Farm
import com.example.nuru.repository.CommentsRepository
import com.example.nuru.repository.MyFarmRepository
import com.google.firebase.firestore.DocumentReference

class MyFarmViewModel(val farmRef : DocumentReference) : ViewModel() {
    /*private val repo = MyFarmRepository(farmRef)

    fun fetchData(): LiveData<MutableList<Farm>> {

        val mutableData = MutableLiveData<MutableList<Farm>>()

        repo.Farm.observeForever{
            mutableData.value = it
        }
        return mutableData
    }*/

    private val repo = MyFarmRepository(farmRef)
    val mutableData = repo.Farm

    fun fetchData(): LiveData<MutableList<Farm>> {
        return mutableData
    }

    fun updateFarm(){
        repo.updateFarm()
    }
}