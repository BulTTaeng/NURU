package com.example.nuru.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.viewmodel.community.EditCommunityViewModel

class ViewModelFactoryForEditCommunityViewModel (private val editInfo: CommunityDTO) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EditCommunityViewModel::class.java)) {
            EditCommunityViewModel(editInfo) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}