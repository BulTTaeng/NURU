package com.example.nuru.view.activity.community

import android.app.Activity
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
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.view.adapter.AddCommunityAdapter
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.activity.map.MapsActivity
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        val intent: Intent = getIntent()

        val communityContents = intent.getParcelableExtra<CommunityDTO>("COMMUNITYENTITY")

        if(communityContents != null) {
            binding.communityDto = communityContents
            val uriList = ArrayList<Uri>()

            for(it in binding.communityDto!!.image){
                uriList.add(Uri.parse(it))
            }

            val adapter = AddCommunityAdapter(uriList, this)
            addImage_recycleView.layoutManager = GridLayoutManager(this ,3)
            addImage_recycleView.adapter = adapter

            btn_Upload.visibility = View.GONE
        }
        else{
            btn_UploadEdit.visibility = View.GONE
        }
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

        btn_Upload.isEnabled = false

        val temparr = ArrayList<String>()
        val createdAt = FieldValue.serverTimestamp()
        val uriToString = ArrayList<String>()
        for(it in selectedImageUriList){
            uriToString.add(it.toString())
        }

        val communityEntity = CommunityEntity(
            uriToString,
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
                val Intent = Intent()

                Intent.putExtra("UPLOAD_DONE", "OK")

                setResult(Activity.RESULT_OK , Intent)
                finish()
            }
            else{
                Toast.makeText(getContext() , getString(R.string.try_later) , Toast.LENGTH_LONG).show()
                progressBar_addCommunity.visibility = View.GONE
                btn_Upload.isEnabled = true
            }
        }
    }

    fun btnEdit(view : View){
        btn_UploadEdit.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            progressBar_addCommunity.visibility = View.VISIBLE

            var success = false

            var re = binding.communityDto!!
            re.title = edt_Title.text.toString()
            re.contents = edt_Contents.text.toString()

            re.image.clear()

            if(selectedImageUriList.isNotEmpty()){
                Log.d("newoooooo" , selectedImageUriList.toString())
                for(image in selectedImageUriList){
                    re.image.add(image.toString())
                }
                Log.d("after " , re.image.toString())
            }

            CoroutineScope(Dispatchers.IO).launch {
                success = viewModel.editCommunity(re)
            }.join()
            if(success){
                finish()
            }
            else{
                progressBar_addCommunity.visibility = View.GONE
                Toast.makeText(getContext() , getContext().getString(R.string.try_later) , Toast.LENGTH_LONG).show()
                btn_UploadEdit.isEnabled = true
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