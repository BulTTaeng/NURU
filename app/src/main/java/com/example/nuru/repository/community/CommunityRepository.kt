package com.example.nuru.repository.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.community.CommunityEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CommunityRepository {

    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    val _mutableData = MutableLiveData<MutableList<CommunityEntity>>()
    val Community: LiveData<MutableList<CommunityEntity>>
        get() = _mutableData

    init {
        CoroutineScope(Dispatchers.IO).launch {
            updateCommunity()
        }
    }

    suspend fun updateCommunity(){
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