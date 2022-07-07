package com.example.nuru.ViewModel.Farm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.Model.Data.Farm.Farm
import com.example.nuru.Repository.Farm.MyFarmRepository
import com.google.firebase.firestore.DocumentReference

class MyFarmViewModel(val farmRef : DocumentReference) : ViewModel() {
    private val repo = MyFarmRepository(farmRef)
    private val farmData = repo.Farm

    fun fetchData(): LiveData<MutableList<Farm>> {
        return farmData
    }

    fun updateFarm(){
        repo.updateFarm()
    }
}