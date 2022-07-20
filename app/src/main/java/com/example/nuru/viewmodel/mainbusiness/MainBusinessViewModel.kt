package com.example.nuru.viewmodel.mainbusiness

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.community.Comments
import com.example.nuru.model.data.mainbusiness.MainBusinessEntity
import com.example.nuru.repository.community.CommentsRepository
import com.example.nuru.repository.mainbusiness.MainBusinessRepository
import com.google.firebase.firestore.CollectionReference


class MainBusinessViewModel() : ViewModel() {

    private val repo = MainBusinessRepository()
    private val mainBusinessData = repo.mainBusinessData

    fun fetchData(): LiveData<MutableList<MainBusinessEntity>> {
        return mainBusinessData
    }

    suspend fun getInfo() : Boolean{
        return repo.getInfo()
    }


}