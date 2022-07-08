package com.example.nuru.viewmodel.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.community.Comments
import com.example.nuru.repository.community.CommentsRepository
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