package com.example.nuru.View.Activity.Community

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nuru.R
import com.example.nuru.View.Adapter.AddCommunityAdapter
import com.example.nuru.Utility.GetCurrentContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_community.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCommunityActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var UserId : String

    var selectedImageUri : Uri? = null

    var singletonC = GetCurrentContext.getInstance()

    var selectedImageUrilist = ArrayList<Uri>()
    lateinit var adapter : AddCommunityAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_community)
        singletonC.setcurrentContext(this)
        firebaseAuth = FirebaseAuth.getInstance()

        UserId = firebaseAuth.currentUser!!.uid

        btn_AddImageInCommunity.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 9981)
        }

        btn_Upload.setOnClickListener {
            uploadImageTOFirebase(selectedImageUrilist)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9981 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUrilist.clear()
            if (data?.clipData != null) { // 사진 여러개 선택한 경우
                val count = data.clipData!!.itemCount
                if (count > 9) {
                    Toast.makeText(applicationContext, getString(R.string.image_limit_9), Toast.LENGTH_LONG)
                    return
                }
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImageUrilist.add(imageUri)
                }

            }
            else { // 단일 선택
                data?.data?.let { uri ->
                    val imageUri : Uri? = data?.data
                    if (imageUri != null) {
                        selectedImageUrilist.add(imageUri)
                    }
                }
            }

            adapter = AddCommunityAdapter(selectedImageUrilist, this)
            addImage_recycleView.layoutManager = GridLayoutManager(this ,3)
            addImage_recycleView.adapter = adapter

            //selectedImageUri = data.data
            //img_ImageInCommunity.setImageURI(selectedImageUri)
        }
    }

    fun uploadImageTOFirebase(uriList: ArrayList<Uri>) {
        if (uriList == null || uriList.isEmpty()) {
            var temp = ArrayList<String>()
            val docRef = db.collection("community")

            val createdAt = FieldValue.serverTimestamp()
            var temparr = ArrayList<String>()
            val temp_int : Long = 0
            val data = hashMapOf(
                "contents" to edt_Contents.text.toString(),
                "title" to edt_Title.text.toString(),
                "writer" to UserId.toString(),
                "image" to temp,
                "time" to createdAt,
                "likeId" to temparr,
                "commentsNum" to temp_int,
            )

            docRef.add(data).addOnSuccessListener {
                finish()
            }
        } else {
            var storage: FirebaseStorage? =
                FirebaseStorage.getInstance()   //FirebaseStorage 인스턴스 생성
            var i = 1
            var temp = ArrayList<String>()

            for(it in uriList){
                var fileName = "COMMUNITY_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}" + i.toString() + "_.png"
                var imagesRef = storage!!.reference.child("community/")
                    .child(fileName)    //기본 참조 위치/images/${fileName}
                //이미지 파일 업로드
                var uploadTask = imagesRef.putFile(it)
                //uploadTaskList.add(uploadTask)

                val urlTask = uploadTask.continueWithTask { task ->
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

                        temp.add(downloadUri.toString())
                        if(temp.size == uriList.size){
                            addTofirebase(temp)
                        }

                    }
                }
                i++
            }
        }
    }

    fun addTofirebase(temp : ArrayList<String>){
        val docRef = db.collection("community")

        val createdAt = FieldValue.serverTimestamp()

        val data = hashMapOf(
            "contents" to edt_Contents.text.toString(),
            "title" to edt_Title.text.toString(),
            "writer" to UserId.toString(),
            "image" to temp,
            "time" to createdAt,
            "commentsNum" to 0,
            "likeId" to ArrayList<String>()
        )

        docRef.add(data).addOnSuccessListener {
            finish()
        }.addOnFailureListener{
        }
    }

}