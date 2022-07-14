package com.example.nuru.view.activity.community

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_community.*
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nuru.R
import com.example.nuru.databinding.ActivityEditCommunityBinding
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.adapter.AddCommunityAdapter
import com.example.nuru.viewmodel.community.EditCommunityViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForEditCommunityViewModel
import kotlinx.android.synthetic.main.activity_add_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class EditCommunityActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    var singletonC = GetCurrentContext.getInstance()

    lateinit var imm : InputMethodManager

    lateinit var editCommunityViewModel : EditCommunityViewModel
    var selectedImageUriList = ArrayList<Uri>()
    private lateinit var binding: ActivityEditCommunityBinding
    lateinit var info : CommunityDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_community)
        binding.activity = this@EditCommunityActivity
        singletonC.setcurrentContext(this)
        progressBar_editCommunity.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val intent: Intent = getIntent()

        binding.communityContents = intent.getParcelableExtra<CommunityDTO>("COMMUNITYENTITY")

        val uriList = ArrayList<Uri>()

        for(it in binding.communityContents!!.image){
            uriList.add(Uri.parse(it))
        }

        val re = binding.communityContents!!

        val adapter = AddCommunityAdapter(uriList, this)
        community_recycleViewInEditCommunityContents.layoutManager = GridLayoutManager(this ,3)
        community_recycleViewInEditCommunityContents.adapter = adapter

        val docRef = db.collection("community").document(binding.communityContents!!.id)

        editCommunityViewModel = ViewModelProvider(this , ViewModelFactoryForEditCommunityViewModel(re))
            .get(EditCommunityViewModel::class.java)

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
            val adapter = AddCommunityAdapter(selectedImageUriList, this)
            community_recycleViewInEditCommunityContents.layoutManager = GridLayoutManager(this ,3)
            community_recycleViewInEditCommunityContents.adapter = adapter
        }
    }

    fun btnEdit(view : View?){
        CoroutineScope(Dispatchers.Main).launch {
            progressBar_editCommunity.visibility = View.VISIBLE

            var success = false

            var re = binding.communityContents!!
            re.title = edt_TitleInEdit.text.toString()
            re.contents = edt_ContentsInEdit.text.toString()

            if(selectedImageUriList.isNotEmpty()){
                Log.d("newoooooo" , selectedImageUriList.toString())
                re.image.clear()
                for(image in selectedImageUriList){
                    re.image.add(image.toString())
                }
                Log.d("after " , re.image.toString())
            }

            Log.d("here!" , re.toString())

            CoroutineScope(Dispatchers.IO).launch {
                success = editCommunityViewModel.editContents(re)
            }.join()
            if(success){
                finish()
            }
            else{
                progressBar_editCommunity.visibility = View.GONE
                Toast.makeText(getContext() , getContext().getString(R.string.try_later) , Toast.LENGTH_LONG).show()
            }

        }

    }

    fun getContext() : EditCommunityActivity{
        return this
    }

    fun btnChangeImage(view : View){

        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 9981)
    }



    private fun showKeyboard(editText: EditText) {
        imm.showSoftInput(editText, 0)
    }

}