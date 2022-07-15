package com.example.nuru.viewmodel.community

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.repository.community.CommunityRepository
import com.example.nuru.utility.paging3.community.CommunityPagingSource

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
    private val communityData1 = repo.Community

    val communityData = Pager(
        PagingConfig(
            pageSize = 30,
            enablePlaceholders = false,
            initialLoadSize = 30,
        ),
        pagingSourceFactory ={  repo.getCommunity()  }
    ).flow.cachedIn(viewModelScope)

    /*fun fetchData(): LiveData<MutableList<CommunityDTO>> {
        return communityData1
    }

    suspend fun updateView(){

        repo.updateCommunity()
    }*/

    suspend fun uploadCommunity(communityEntity: CommunityEntity) : Boolean{
        return repo.uploadCommunity(communityEntity)
    }

    suspend fun editCommunity(communityDto : CommunityDTO) : Boolean{
        return repo.editCommunity(communityDto)
    }


}