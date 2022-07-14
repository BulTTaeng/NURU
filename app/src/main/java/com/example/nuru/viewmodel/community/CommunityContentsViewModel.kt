package com.example.nuru.viewmodel.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.repository.community.CommunityContentsRepository
import com.google.firebase.firestore.DocumentReference

class CommunityContentsViewModel(val communityContentsRef : DocumentReference) :ViewModel() {
    private val repo = CommunityContentsRepository(communityContentsRef)
    private val contentsData = repo.CommunityContents

    fun fetchData(): LiveData<CommunityDTO> {
        return contentsData
    }

    fun updateCommunityContents(){
        repo.updateCommunityContents()
    }

    suspend fun deleteCommunityAndComments() : Boolean{
        return repo.deleteCommunityAndComments()
    }

    fun deleteLike(){
        repo.deleteLike()
    }

    fun addLike(){
        repo.addLike()
    }

    suspend fun addComments(commentstxt : String):Boolean{
        return repo.addComments(commentstxt);
    }
}