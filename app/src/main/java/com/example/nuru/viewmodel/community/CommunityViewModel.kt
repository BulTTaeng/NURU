package com.example.nuru.viewmodel.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.repository.community.CommunityRepository

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
    private val communityData = repo.Community

    fun fetchData(): LiveData<MutableList<CommunityEntity>> {
        return communityData
    }

    suspend fun updateView(){

        repo.updateCommunity()
    }
}