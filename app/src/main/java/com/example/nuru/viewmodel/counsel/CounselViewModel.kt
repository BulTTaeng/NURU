package com.example.nuru.viewmodel.counsel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.counsel.NameCard
import com.example.nuru.repository.counsel.CounselRepository

class CounselViewModel : ViewModel() {
    private val repo = CounselRepository()
    private val nameCardList = repo.getNameCardList

    fun fetchData() : LiveData<MutableList<NameCard>> {
        return nameCardList
    }
}