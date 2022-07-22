package com.example.nuru.repository.alarm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class AlarmRepository {

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    var communityList  = ArrayList<String>()
    var farmList = ArrayList<String>()
    var mainBusinessList = ArrayList<String>()

    val _mutableCommunityData = MutableLiveData<MutableList<String>>()
    val communityData: LiveData<MutableList<String>>
        get() = _mutableCommunityData

    val _mutableFarmData = MutableLiveData<MutableList<String>>()
    val farmData: LiveData<MutableList<String>>
        get() = _mutableFarmData

    val _mutableMainBusinessData = MutableLiveData<MutableList<String>>()
    val mainBusinessData: LiveData<MutableList<String>>
        get() = _mutableMainBusinessData



    init {
    }

    suspend fun getCommunityAlarm() : Boolean{
        return try {
            communityList.clear()
            db.collection("alarm").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                communityList = it["community"] as ArrayList<String>
                _mutableCommunityData.value = communityList
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }

    suspend fun getFarmAlarm() : Boolean{
        return try {
            farmList.clear()
            db.collection("alarm").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                farmList = it["farm"] as ArrayList<String>
                _mutableFarmData.value = farmList
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }

    suspend fun getMainBusinessAlarm() : Boolean{
        return try {
            mainBusinessList.clear()
            db.collection("mainBusiness").orderBy("time" , Query.Direction.DESCENDING).limit(5).get().addOnSuccessListener { snapshot ->

                for(it in snapshot){
                    mainBusinessList.add(it["title"].toString() + "정책 정보가 업데이트 되었습니다.")
                }
                _mutableMainBusinessData.value = mainBusinessList
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }




}