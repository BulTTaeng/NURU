package com.example.nuru.repository.farm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.farm.Farm
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class MyFarmRepository(val farmRef : DocumentReference) {
    val db = FirebaseFirestore.getInstance()

    val _mutableData = MutableLiveData<MutableList<Farm>>()
    val Farm: LiveData<MutableList<Farm>>
        get() = _mutableData

    init {
        updateFarm()
    }

    fun updateFarm(){
        farmRef.get().addOnSuccessListener {
            var i =1

            var user_farm_info = ArrayList<Farm>()

            val username = it["name"] as String
            var userEmail = it["email"] as String
            val user_farm_list = it["farmList"] as ArrayList<String>
            //Log.d("userName", "DocumentSnapshot data: ${username} ")
            //txt_UserNameMyPage.text = username + "\n" +userEmail
            //Log.d("userName", "DocumentSnapshot data: ${document.data}")
            if (user_farm_list.isEmpty()) {
                user_farm_list.add("zerozero")
            }
            val Farm_Info = db.collection("farmList")

            Farm_Info.whereIn("farmId", user_farm_list).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("newwww", document.data["farmName"].toString())
                        val lati = document.data["latitude"] as Double
                        val longti = document.data["longitude"] as Double

                        val location = LatLng(lati, longti)

                        user_farm_info.add(
                            Farm(
                                document.id,
                                document.data["farmName"].toString(),
                                document.data["farmPhoto"] as ArrayList<String>,
                                location,
                                document.data["farmAddress"].toString(),
                                document.data["farmOwner"].toString(),
                                i,
                                document.data["products"].toString()
                            )
                        )

                        i++

                    }
                    _mutableData.value = user_farm_info
                }
        }
    }
}