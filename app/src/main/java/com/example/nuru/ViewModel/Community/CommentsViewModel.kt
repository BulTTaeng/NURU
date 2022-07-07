package com.example.nuru.ViewModel.Community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.Model.Data.Community.Comments
import com.example.nuru.Repository.Community.CommentsRepository
import com.google.firebase.firestore.CollectionReference

class CommentsViewModel(val commentsRef : CollectionReference) : ViewModel() {
    /*private val repo = CommentsRepository(commentsRef)

    fun fetchData(): LiveData<MutableList<Comments>> {

        val mutableData = MutableLiveData<MutableList<Comments>>()

        repo.Comments.observeForever{
            mutableData.value = it
        }
        return mutableData
    }*/

    private val repo = CommentsRepository(commentsRef)
    private val commentsData = repo.Comments

    fun fetchData(): LiveData<MutableList<Comments>> {
        return commentsData
    }

    fun updateComments(){
        repo.updateComments()
    }


}