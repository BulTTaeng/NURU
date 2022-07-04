package com.example.nuru.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.CommunityContentsActivity
import com.example.nuru.model.Comments
import com.example.nuru.model.CommunityEntity
import com.example.nuru.model.Farm
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_my_page.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class MyFarmRepository(val farmRef : DocumentReference) {
    val db = FirebaseFirestore.getInstance()

    val _mutableData = MutableLiveData<MutableList<Farm>>()
    val Farm: LiveData<MutableList<Farm>>
        get() = _mutableData



    fun getData(): LiveData<MutableList<Farm>> {

        /*db.collection("community").orderBy("time" , Query.Direction.DESCENDING).get().addOnSuccessListener{
            var community_info = ArrayList<CommunityEntity>()
            for(document in it){
                val img_path = document["image"] as ArrayList<String>
                val contents = document["contents"].toString()
                val title = document["title"].toString()
                val writer = document["writer"].toString()
                val like = document["likeId"] as ArrayList<String>
                val comments = document["commentsNum"] as Long
                val time = document.getTimestamp("time")?.toDate()!!

                var idd= document.id

                community_info.add(
                    CommunityEntity(img_path[0],contents,title , writer , idd , like , comments , time)
                )

            }
            _mutableData.value = community_info

        }*/

        return _mutableData
    }

    init {

        farmRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                var i = 1

                var user_farm_info = ArrayList<Farm>()

                val username = snapshot["name"] as String
                var userEmail = snapshot["email"] as String
                val user_farm_list = snapshot["farmList"] as ArrayList<String>
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
}