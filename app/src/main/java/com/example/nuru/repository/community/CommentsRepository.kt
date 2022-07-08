package com.example.nuru.repository.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.community.Comments
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

class CommentsRepository(val commentsRef : CollectionReference) {
    val db = FirebaseFirestore.getInstance()

    val _mutableData = MutableLiveData<MutableList<Comments>>()
    val Comments: LiveData<MutableList<Comments>>
        get() = _mutableData

    init {

        updateComments()
    }

    fun updateComments(){
        commentsRef.orderBy("time" , Query.Direction.DESCENDING).get().addOnSuccessListener {

            var comments_info = ArrayList<Comments>()

            it.forEach{
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
