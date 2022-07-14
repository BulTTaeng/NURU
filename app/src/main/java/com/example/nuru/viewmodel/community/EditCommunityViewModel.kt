package com.example.nuru.viewmodel.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.repository.community.EditCommunityRepository

class EditCommunityViewModel(val editDTO : CommunityDTO) : ViewModel() {

    private val repo = EditCommunityRepository(editDTO)
    private val editData = repo.CommunityEdit

    fun fetchData(): LiveData<CommunityDTO> {
        return editData
    }

    suspend fun editContents(editCommunityDTO: CommunityDTO) : Boolean{
        return repo.editCommunity(editCommunityDTO)
    }
}