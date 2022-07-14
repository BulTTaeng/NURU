package com.example.nuru.view.activity.community

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nuru.R
import com.example.nuru.databinding.ActivityAddCommunityBinding
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.view.adapter.AddCommunityAdapter
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCommunityActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var UserId : String

    var singletonC = GetCurrentContext.getInstance()

    var selectedImageUriList = ArrayList<Uri>()
    lateinit var adapter : AddCommunityAdapter
    private lateinit var binding: ActivityAddCommunityBinding

    private val viewModel :CommunityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_community)
        binding.activity = this@AddCommunityActivity
        singletonC.setcurrentContext(this)
        firebaseAuth = FirebaseAuth.getInstance()
        progressBar_addCommunity.visibility = View.GONE

        UserId = firebaseAuth.currentUser!!.uid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9981 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUriList.clear()
            if (data?.clipData != null) { // 사진 여러개 선택한 경우
                val count = data.clipData!!.itemCount
                if (count > 9) {
                    Toast.makeText(applicationContext, getString(R.string.image_limit_9), Toast.LENGTH_LONG)
                    return
                }
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImageUriList.add(imageUri)
                }

            }
            else { // 단일 선택
                data?.data?.let { uri ->
                    val imageUri : Uri? = data?.data
                    if (imageUri != null) {
                        selectedImageUriList.add(imageUri)
                    }
                }
            }

            //TODO:: 여기도 listAdapter 필요?
            adapter = AddCommunityAdapter(selectedImageUriList, this)
            addImage_recycleView.layoutManager = GridLayoutManager(this ,3)
            addImage_recycleView.adapter = adapter
        }
    }

    fun btnUpload(view : View){
        val temparr = ArrayList<String>()
        val createdAt = FieldValue.serverTimestamp()

        val communityEntity = CommunityEntity(
            selectedImageUriList ,
            edt_Contents.text.toString(),
            edt_Title.text.toString(),
            UserId.toString(),
            temparr,
            0,
            createdAt
        )

        var success : Boolean = false
        progressBar_addCommunity.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).launch {
                success = viewModel.uploadCommunity(communityEntity)
            }.join()

            if(success){
                finish()
            }
            else{
                Toast.makeText(getContext() , getString(R.string.try_later) , Toast.LENGTH_LONG).show()
                progressBar_addCommunity.visibility = View.GONE
            }
        }
    }

    fun btnAddImageInCommunity(view :View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 9981)
    }

    fun getContext() : AddCommunityActivity{
        return this
    }



}