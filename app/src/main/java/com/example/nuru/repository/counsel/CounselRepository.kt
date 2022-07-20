package com.example.nuru.repository.counsel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.counsel.NameCard
import com.google.firebase.firestore.FirebaseFirestore

class CounselRepository {
    val db = FirebaseFirestore.getInstance()
    val colRef = db.collection("tel")

    val _mutableData = MutableLiveData<MutableList<NameCard>>()
    val getNameCardList : LiveData<MutableList<NameCard>> get() = _mutableData

    init {
        initalizeNameCardList()
    }

    fun initalizeNameCardList() {
        var tempList = ArrayList<NameCard>()

        colRef.get().addOnSuccessListener { docs ->
            for (doc in docs) {
                tempList.add(NameCard.NameCardMapper.fromDB(doc))
            }
            _mutableData.value = tempList
        }
    }
}