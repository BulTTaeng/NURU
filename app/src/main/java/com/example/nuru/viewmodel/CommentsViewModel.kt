package com.example.nuru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.Comments
import com.example.nuru.repository.CommentsRepository
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
    val mutableData = repo.Comments

    fun fetchData(): LiveData<MutableList<Comments>> {
        return mutableData
    }

    fun updateComments(){
        repo.updateComments()
    }


}