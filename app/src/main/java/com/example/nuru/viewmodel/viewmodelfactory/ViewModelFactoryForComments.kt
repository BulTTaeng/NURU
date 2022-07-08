package com.example.nuru.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nuru.viewmodel.community.CommentsViewModel
import com.google.firebase.firestore.CollectionReference

class ViewModelFactoryForComments(val Ref : CollectionReference) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            CommentsViewModel(Ref) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}