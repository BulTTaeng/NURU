package com.example.nuru.repository.mainbusiness

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.community.Comments
import com.example.nuru.model.data.mainbusiness.MainBusinessEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MainBusinessRepository {

    val db = FirebaseFirestore.getInstance()
    var list  = ArrayList<MainBusinessEntity>()

    val _mutableData = MutableLiveData<MutableList<MainBusinessEntity>>()
    val mainBusinessData: LiveData<MutableList<MainBusinessEntity>>
        get() = _mutableData

    init {
    }

    suspend fun getInfo():Boolean{
        return try{
            list.clear()
            db.collection("mainBusiness").get().addOnSuccessListener { snapshot ->
                for(it in snapshot){
                    list.add(MainBusinessEntity( it["title"].toString() ,it["contents"].toString() , it["link"].toString() ))
                }
                _mutableData.value = list
                true
            }.addOnFailureListener{
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }
}