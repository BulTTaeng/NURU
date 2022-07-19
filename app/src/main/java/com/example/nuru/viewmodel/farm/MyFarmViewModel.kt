package com.example.nuru.viewmodel.farm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.model.data.farm.FarmEntity
import com.example.nuru.repository.farm.MyFarmRepository
import com.google.firebase.firestore.DocumentReference

class MyFarmViewModel(val farmRef : DocumentReference) : ViewModel() {
    private val repo = MyFarmRepository(farmRef)
    private val farmData = repo.Farm

    private val farmImages  = repo.images

    fun fetchData(): LiveData<MutableList<Farm>> {
        return farmData
    }

    fun fetchImages() : LiveData<MutableList<String>>{
        return farmImages
    }

    fun updateFarm(){
        repo.updateFarm()
    }

    suspend fun addFarm(farmEntity : FarmEntity) : Boolean{
        return repo.addFarm(farmEntity)
    }

    //////////////////////AddAdmin//////////////////////

    suspend fun addAdmin(addId : String , farmId : String) : Boolean{
        return repo.addAdmin(addId, farmId)
    }

    suspend fun getUserNameAndEmail(userId : String) : ArrayList<String> {
        return repo.getUserNameAndEmail(userId)
    }

    suspend fun updateImage(id : String) : Boolean{
        return repo.updateImage(id)
    }

}