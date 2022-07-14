package com.example.nuru.repository.community

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.model.data.community.CommunityEntity
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommunityRepository {

    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    val _mutableData = MutableLiveData<MutableList<CommunityDTO>>()
    val Community: LiveData<MutableList<CommunityDTO>>
        get() = _mutableData

    var firabaseStorage = FirebaseStorage.getInstance()
    val communityRef = db.collection("community")

    val emptyHash = hashMapOf(
        "list" to ArrayList<String>()
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            updateCommunity()
        }
    }

    suspend fun updateCommunity(){
        db.collection("community")
            .orderBy("time" , Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                var community_info = ArrayList<CommunityDTO>()

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
                            CommunityDTO(img_path,contents,title , writer , idd , like , comments , timedate)
                        )
                    }
                    else{
                        val timedate = Date()
                        community_info.add(
                            CommunityDTO(img_path,contents,title , writer , idd , like , comments , timedate)
                        )
                    }

                    _mutableData.value = community_info
                }

            }
    }

    suspend fun uploadCommunity(communityEntity: CommunityEntity) : Boolean{

        return try {

            if (communityEntity.image == null || communityEntity.image.isEmpty()) { //NO Image
                addTofirebase(communityEntity , ArrayList<String>())

            } else {
                addTofirebase(communityEntity, addToStorage(communityEntity))
                true
            }
            true
        } catch (e: Exception) {
            when(e){
                is FirebaseException ->{
                    Log.d("FirebaseException" , e.toString())
                }
                else ->{
                    Log.d("Exception", e.toString())
                }
            }
            false
        }
    }

    suspend fun addTofirebase(communityEntity: CommunityEntity , temp : ArrayList<String>){
        val docRef = db.collection("community")

        val createdAt = FieldValue.serverTimestamp()

        val data = hashMapOf(
            "contents" to communityEntity.contents,
            "title" to communityEntity.title,
            "writer" to communityEntity.writer,
            "image" to temp,
            "time" to createdAt,
            "commentsNum" to 0,
            "likeId" to ArrayList<String>()
        )

        var id : String =""
        docRef.add(data).addOnSuccessListener {
            id = it.id
        }.addOnFailureListener{
        }.await()

        db.collection("comments").document(id).set(emptyHash).addOnCompleteListener {
            if (it.isSuccessful) {
                true
            } else {
                Log.d("문제 발생", "aaaaaaaaaaaaaaaaaaa")
                false
            }
        }
    }

    suspend fun addToStorage(communityEntity: CommunityEntity) : ArrayList<String>{
        var i = 1
        var images = ArrayList<String>()

        for (it in communityEntity.image) {
            var fileName =
                "COMMUNITY_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}" + i.toString() + "_.png"
            var imagesRef = firabaseStorage!!.reference.child("community/")
                .child(fileName)    //기본 참조 위치/images/${fileName}
            //이미지 파일 업로드
            var uploadTask = imagesRef.putFile(Uri.parse(it))
            //uploadTaskList.add(uploadTask)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imagesRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    //downloadUri 가 firebase storage 참조 주소

                    images.add(downloadUri.toString())

                }
            }.await()

            i++
        }

        return images
    }
/////////////////////Edit///////////////////////

    suspend fun editCommunity(editCommunityDTO: CommunityDTO) : Boolean{

        val docRef = db.collection("community").document(editCommunityDTO.id)

        editContents(editCommunityDTO)

        return try {

            editContents(editCommunityDTO)
            //_mutableData.value!!.contents = editCommunityDTO.contents
            //_mutableData.value!!.title = editCommunityDTO.title


            if (editCommunityDTO.image.isNotEmpty()){

                val communityEntity = CommunityEntity(
                    editCommunityDTO.image,
                    editCommunityDTO.contents,
                    editCommunityDTO.title,
                    editCommunityDTO.writer,
                    editCommunityDTO.like,
                    editCommunityDTO.comments,
                    FieldValue.serverTimestamp()
                )

                docRef.update("image", addToStorage(communityEntity))

                true
            }
            true
        } catch (e: Exception) {
            when(e){
                is FirebaseException ->{
                    Log.d("FirebaseException" , e.toString())
                }
                else ->{
                    Log.d("Exception", e.toString())
                }
            }
            false
        }
    }

    private suspend fun editContents(editCommunityDTO: CommunityDTO){

        Log.d("0000000000" , editCommunityDTO.toString())
        var ok = false
        val docRef = db.collection("community").document(editCommunityDTO.id)
        //여기서 왜 .add Listener 달면 null pointer exception이 뜨지???
        docRef.update("contents", editCommunityDTO.contents, "title", editCommunityDTO.title).await()
    }


}