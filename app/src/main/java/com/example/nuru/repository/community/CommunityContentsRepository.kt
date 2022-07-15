package com.example.nuru.repository.community

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.community.CommunityDTO
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class CommunityContentsRepository(val communityContentsRef : DocumentReference) {
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    val _mutableData = MutableLiveData<CommunityDTO>()
    val CommunityContents: LiveData<CommunityDTO>
        get() = _mutableData
    var firabaseStorage = FirebaseStorage.getInstance()

    init {
        updateCommunityContents()
    }

    fun updateCommunityContents(){
        var newContents : CommunityDTO

        CoroutineScope(Dispatchers.IO).async {
            //widget_progressbarInCommunityImage.visibility = View.VISIBLE
            communityContentsRef.get().addOnSuccessListener {
                if (it["title"] != null) {
                    newContents = CommunityDTO(
                        it["image"] as ArrayList<String>,
                        it["contents"].toString(),
                        it["title"].toString(),
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

    suspend fun deleteCommunityAndComments() : Boolean{
        return try {
            db.collection("community").document(_mutableData.value?.id.toString()).delete().await()
            db.collection("comments").document(_mutableData.value?.id.toString()).get().addOnCompleteListener{
                if(it.isSuccessful){
                    val list = it.result["list"] as List<String>
                    for(docId in list){
                        db.collection("comments").document(_mutableData.value?.id.toString())
                            .collection(_mutableData.value?.id.toString()).document(docId).delete()
                    }

                    db.collection("comments").document(_mutableData.value?.id.toString()).delete()

                }
                else{
                    Log.d("FFFF" , "FFFFF")
                }
            }

            true
        }catch (e : FirebaseException){
            Log.e("firesbase " , e.toString())
            false
        }
    }

    fun deleteLike(){
        db.collection("community").document(_mutableData.value!!.id)
            .update("likeId", FieldValue.arrayRemove(firebaseAuth.currentUser?.uid))
        updateLike()
    }

    fun addLike(){
        db.collection("community").document(_mutableData.value!!.id)
            .update("likeId", FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
        updateLike()
    }

    fun updateLike(){
        db.collection("community").document(_mutableData.value!!.id).get().addOnCompleteListener{
            _mutableData.value!!.like = it.result["likeId"] as ArrayList<String>
            _mutableData.value = _mutableData.value
        }
    }

    suspend fun addComments(commentstxt : String) : Boolean{
        val createTime = FieldValue.serverTimestamp()
        var nameofwriter =""
        val commentsRef = db.collection("comments").document(_mutableData.value!!.id).collection(_mutableData.value!!.id)

        return try {

            db.collection("user").document(firebaseAuth.currentUser?.uid.toString()).get().addOnCompleteListener{
                if(it.isSuccessful) {
                    nameofwriter = it.result["name"].toString()
                    true
                }
                else{
                    false
                }
            }.await()


            val data = hashMapOf(
                "contents" to commentstxt,
                "id" to "",
                "communityId" to _mutableData.value!!.id,
                "time" to createTime,
                "writer" to firebaseAuth.currentUser?.uid,
                "name" to nameofwriter
            )

            commentsRef.add(data).addOnSuccessListener {
                val data = hashMapOf(
                    "id" to it.id
                )
                commentsRef.document(it.id).update("id" , it.id)
                db.collection("comments").document(_mutableData.value!!.id).update("list", FieldValue.arrayUnion(it.id))
            }.addOnFailureListener{
                false
            }

            commentsRef.get().addOnSuccessListener {
                //TODO :: 동시접근 이슈 있음. DeleteComment도 마찬가지
                var size : Long = 0
                db.collection("community").document(_mutableData.value!!.id).get().addOnSuccessListener {
                    size = it["commentsNum"] as Long
                    size++
                    db.collection("community").document(_mutableData.value!!.id).update("commentsNum" , size)
                    _mutableData.value!!.comments = size
                    _mutableData.value = _mutableData.value
                }.addOnFailureListener{
                    false
                }


            }
            true
        }catch (e:Exception){
            Log.d("Error" , e.toString())
            false
        }
    }

}