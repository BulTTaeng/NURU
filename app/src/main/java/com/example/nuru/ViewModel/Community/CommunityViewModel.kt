package com.example.nuru.ViewModel.Community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.Model.Data.Community.CommunityEntity
import com.example.nuru.Repository.Community.CommunityRepository

class CommunityViewModel : ViewModel() {
    /*private val repo = CommunityRepository()

    fun fetchData(): LiveData<MutableList<CommunityEntity>> {

        val mutableData = MutableLiveData<MutableList<CommunityEntity>>()

        repo.Community.observeForever{
            mutableData.value = it
        }
        return mutableData
    }*/

    private val repo = CommunityRepository()
    val mutableData = repo.Community

    fun fetchData(): LiveData<MutableList<CommunityEntity>> {
        return mutableData
    }

    fun updateView(){
        repo.updateCommunity()
    }
}