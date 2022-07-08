package com.example.nuru.viewmodel.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.community.Comments
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.repository.community.CommentsRepository
import com.example.nuru.repository.community.CommunityContentsRepository
import com.google.firebase.firestore.DocumentReference

class CommunityContentsViewModel(val communityContentsRef : DocumentReference) :ViewModel() {
    private val repo = CommunityContentsRepository(communityContentsRef)
    private val contentsData = repo.CommunityContents

    fun fetchData(): LiveData<CommunityEntity> {
        return contentsData
    }

    fun updateCommunityContents(){
        repo.updateCommunityContents()
    }
}