package com.example.nuru.repository.community

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.model.data.community.CommunityDTO
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class EditCommunityRepository(val editDTO : CommunityDTO) {

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    var firabaseStorage = FirebaseStorage.getInstance()

    val _mutableData = MutableLiveData<CommunityDTO>()
    val CommunityEdit: LiveData<CommunityDTO>
        get() = _mutableData

    init {
        _mutableData.value = editDTO!!
    }


    suspend fun editCommunity(editCommunityDTO: CommunityDTO) : Boolean{

        val docRef = db.collection("community").document(editCommunityDTO.id)

        editContents(editCommunityDTO)

        return try {

            editContents(editCommunityDTO)
            //_mutableData.value!!.contents = editCommunityDTO.contents
            //_mutableData.value!!.title = editCommunityDTO.title


            if (editCommunityDTO.image.isNotEmpty()){

                var i = 1
                var images = ArrayList<String>()

                for (it in editCommunityDTO.image) {
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

                Log.d("@@@@@@@@@@@@" , images.toString())

                docRef.update("image", images)

                val string_to_uri = ArrayList<String>()

                for(it in editCommunityDTO.image){
                    string_to_uri.add(it.toString())
                }

                Log.d("aaaa" , string_to_uri.toString())
                //_mutableData.value!!.image = string_to_uri
                //_mutableData.value = _mutableData.value
                true
            }
            else{

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