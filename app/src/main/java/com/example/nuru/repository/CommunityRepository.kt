package com.example.nuru.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.CommunityEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CommunityRepository {

    var db : FirebaseFirestore
    val _mutableData = MutableLiveData<MutableList<CommunityEntity>>()
    val Community: LiveData<MutableList<CommunityEntity>>
        get() = _mutableData

    init {
        db = FirebaseFirestore.getInstance()
        updateCommunity()
    }

    fun updateCommunity(){
        db.collection("community")
            .orderBy("time" , Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                var community_info = ArrayList<CommunityEntity>()

                it.forEach{
                    val img_path = it["image"] as ArrayList<String>
                    val contents = it["contents"].toString()
                    val title = it["title"].toString()
                    val writer = it["writer"].toString()
                    val like = it["likeId"] as ArrayList<String>
                    val comments = it["commentsNum"] as Long
                    var idd= it.id
                    if(it["time"] != null){
                        val time = it["time"] as Timestamp
                        val timedate = Date(time.seconds * 1000)

                        community_info.add(
                            CommunityEntity(img_path,contents,title , writer , idd , like , comments , timedate)
                        )
                    }
                    else{
                        val timedate = Date()
                        community_info.add(
                            CommunityEntity(img_path,contents,title , writer , idd , like , comments , timedate)
                        )
                    }

                    _mutableData.value = community_info
                }

            }
    }

}