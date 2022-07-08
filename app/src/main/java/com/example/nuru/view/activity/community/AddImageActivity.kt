package com.example.nuru.view.activity.community

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.nuru.R
import com.example.nuru.utility.GetCurrentContext
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_image.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddImageActivity : AppCompatActivity() {

    lateinit var farm_id: String
    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    var selectedImageUri : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)
        singletonC.setcurrentContext(this)

        val Intent = intent
        farm_id = Intent.getStringExtra("FARMID").toString()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        btn_FindImage.setOnClickListener {
            val intent = Intent(android.content.Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            startActivityForResult(intent, 9981)
        }

        btn_UploadImage.setOnClickListener {
            uploadImageTOFirebase(selectedImageUri)
            //var photoPickerIntent = Intent
            //photoPickerIntent.type = "image/*"
            //startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9981 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            img_UploadImage.setImageURI(selectedImageUri)
        }
    }

    fun uploadImageTOFirebase(uri: Uri?) {
        if(uri == null){
            Toast.makeText(this, "이미지가 선택 되지 않았습니다" , Toast.LENGTH_LONG).show()
            return
        }
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()   //FirebaseStorage 인스턴스 생성
        //파일 이름 생성.
        var fileName = "FARM_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"
        //파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하기 위해 참조를 생성.
        //참조는 클라우드 파일을 가리키는 포인터라고 할 수 있음.
        var imagesRef = storage!!.reference.child("images/").child(fileName)    //기본 참조 위치/images/${fileName}
        //이미지 파일 업로드
        var uploadTask = imagesRef.putFile(uri)

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
                img_UploadImage.setImageURI(uri)

                val docRef = db.collection("farmList").document(farm_id)
                var farm_photo = ArrayList<String>()
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            farm_photo = document["farmPhoto"] as ArrayList<String>
                            farm_photo.add(downloadUri.toString())
                            farm_photo.remove(getString(R.string.empty_image))
                            docRef.update("farmPhoto", farm_photo)
                        } else {
                            Log.d("TAG", "No such document")
                        }
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }
            } else {
                // Handle failures
                // ...
            }
        }
    }
}