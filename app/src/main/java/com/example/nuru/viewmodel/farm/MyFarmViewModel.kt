package com.example.nuru.viewmodel.farm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.model.data.farm.FarmDAO
import com.example.nuru.repository.farm.MyFarmRepository
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.activity_add_admin.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MyFarmViewModel(val farmRef : DocumentReference) : ViewModel() {
    private val repo = MyFarmRepository(farmRef)
    private val farmData = repo.Farm

    fun fetchData(): LiveData<MutableList<Farm>> {
        return farmData
    }

    fun updateFarm(){
        repo.updateFarm()
    }

    suspend fun addFarm(farmDao : FarmDAO) : Boolean{
        return repo.addFarm(farmDao)
    }

    //////////////////////AddAdmin//////////////////////

    suspend fun addAdmin(addId : String , farmId : String) : Boolean{
        return repo.addAdmin(addId, farmId)
    }

    suspend fun getUserNameAndEmail(userId : String) : String {
        return repo.getUserNameAndEmail(userId)
    }
}