package com.example.nuru.repository.community

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.model.data.community.Comments
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.view.adapter.ImgInCommunityAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_community_contents.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.ArrayList

class CommunityContentsRepository(val communityContentsRef : DocumentReference) {
    val db = FirebaseFirestore.getInstance()

    val _mutableData = MutableLiveData<CommunityEntity>()
    val CommunityContents: LiveData<CommunityEntity>
        get() = _mutableData

    init {
        updateCommunityContents()
    }

    fun updateCommunityContents(){
        var newContents : CommunityEntity

        CoroutineScope(Dispatchers.IO).async {
            //widget_progressbarInCommunityImage.visibility = View.VISIBLE
            communityContentsRef.get().addOnSuccessListener {
                newContents = CommunityEntity(
                    it["image"] as ArrayList<String> ,
                    it["contents"].toString() ,
                    it["title"].toString() ,
                    it["writer"].toString(),
                    it.id.toString(),
                    it["likeId"] as ArrayList<String>,
                    it["commentsNum"] as Long,
                    Date()
                )
                _mutableData.value = newContents
            }
        }
    }

}