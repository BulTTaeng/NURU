package com.example.nuru.view.activity.community

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.databinding.ActivityAddImageBinding
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
    private lateinit var binding: ActivityAddImageBinding

    //TODO:: update later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_image)
        binding.activity = this@AddImageActivity

        singletonC.setcurrentContext(this)

        val Intent = intent
        farm_id = Intent.getStringExtra("FARMID").toString()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9981 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            img_UploadImage.setImageURI(selectedImageUri)
        }
    }

    fun btnFindImage(view : View){
        val intent = Intent(android.content.Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(intent, 9981)
    }

    fun btnUploadImage(view : View){
        uploadImageTOFirebase(selectedImageUri)
    }

    fun uploadImageTOFirebase(uri: Uri?) {
        if(uri == null){
            Toast.makeText(this, "???????????? ?????? ?????? ???????????????" , Toast.LENGTH_LONG).show()
            return
        }
        var storage: FirebaseStorage? = FirebaseStorage.getInstance()   //FirebaseStorage ???????????? ??????
        //?????? ?????? ??????.
        var fileName = "FARM_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"
        //?????? ?????????, ????????????, ??????, ??????????????? ???????????? ?????? ??????????????? ?????? ?????? ????????? ??????.
        //????????? ???????????? ????????? ???????????? ??????????????? ??? ??? ??????.
        var imagesRef = storage!!.reference.child("images/").child(fileName)    //?????? ?????? ??????/images/${fileName}
        //????????? ?????? ?????????
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
                //downloadUri ??? firebase storage ?????? ??????
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