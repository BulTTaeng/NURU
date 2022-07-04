package com.example.nuru.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.CommunityContentsActivity
import com.example.nuru.model.Comments
import com.example.nuru.model.CommunityEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class CommentsRepository(val commentsRef : CollectionReference) {
    val db = FirebaseFirestore.getInstance()

    val _mutableData = MutableLiveData<MutableList<Comments>>()
    val Comments: LiveData<MutableList<Comments>>
        get() = _mutableData



    fun getData(): LiveData<MutableList<Comments>> {

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

        commentsRef.orderBy("time" , Query.Direction.DESCENDING).addSnapshotListener{
            snapshot , e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if(snapshot != null) {
                val doc = snapshot.documents
                var comments_info = ArrayList<Comments>()

                doc.forEach{
                    val contents = it["contents"].toString()
                    val id = it["id"].toString()
                    val writer = it["writer"].toString()
                    val name = it["name"].toString()
                    val communityId = it["communityId"].toString()

                    if(it["time"] != null){
                        val time = it["time"] as com.google.firebase.Timestamp
                        val timedate = Date(time.seconds * 1000)

                        comments_info.add(
                            Comments(id , communityId, contents, writer , timedate , name)
                        )
                    }
                    else{
                        val timedate = Date()
                        comments_info.add(
                            Comments(id , communityId, contents, writer , timedate , name)
                        )
                    }

                    //val t =  it["time"] as com.google.firebase.Timestamp

                    //val time = Date(t.seconds*1000)

                }
                _mutableData.value = comments_info
            }
        }
    }
}