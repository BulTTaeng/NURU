package com.example.nuru.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nuru.viewmodel.community.CommunityContentsViewModel
import com.google.firebase.firestore.DocumentReference

class ViewModelFactoryForCommunityContents(val Ref : DocumentReference) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CommunityContentsViewModel::class.java)) {
            CommunityContentsViewModel(Ref) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}