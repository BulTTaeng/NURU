package com.example.nuru.view.activity.community

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_community.*
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.databinding.ActivityEditCommunityBinding
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.utility.GetCurrentContext
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditCommunityActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    var singletonC = GetCurrentContext.getInstance()

    lateinit var imm : InputMethodManager

    var selectedImageUri : Uri? = null
    private lateinit var binding: ActivityEditCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_community)
        binding.activity = this@EditCommunityActivity
        singletonC.setcurrentContext(this)

        firebaseAuth = FirebaseAuth.getInstance()

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val intent: Intent = getIntent()

        binding.communityContents = intent.getParcelableExtra<CommunityEntity>("COMMUNITYENTITY")



        //edt_TitleInEdit.setText(title)
        //edt_ContentsInEdit.setText(contents)

        //edt_TitleInEdit.text = Editable.Factory.getInstance().newEditable(title)
        //edt_ContentsInEdit.text = Editable.Factory.getInstance().newEditable(contents)

        val urt = Uri.parse(binding.communityContents!!.image.toString())
        Glide.with(this).load(urt).into(img_EditImage)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9981 && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            img_EditImage.setImageURI(selectedImageUri)
        }
    }

    fun btnEdit(view : View){
        uploadImageTOFirebaseAndAdd(selectedImageUri , binding.communityContents!!.id,
            edt_TitleInEdit.text.toString(), edt_ContentsInEdit.text.toString())
    }
    fun btnChangeImage(view : View){
        val intent = Intent(android.content.Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(intent, 9981)
    }



    private fun showKeyboard(editText: EditText) {
        imm.showSoftInput(editText, 0)
    }

    fun uploadImageTOFirebaseAndAdd(uri: Uri? , id : String , title : String , contents : String) {
        val docRef = db.collection("community").document(id)

        if (uri == null) {
            docRef.update("contents" , contents , "title" , title)
        } else {
            var storage: FirebaseStorage? =
                FirebaseStorage.getInstance()   //FirebaseStorage 인스턴스 생성
            //파일 이름 생성.
            var fileName = "COMMUNITY_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"
            //파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하기 위해 참조를 생성.
            //참조는 클라우드 파일을 가리키는 포인터라고 할 수 있음.
            var imagesRef = storage!!.reference.child("community/")
                .child(fileName)    //기본 참조 위치/images/${fileName}
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
                    var temp = ArrayList<String>()
                    temp.add(downloadUri.toString())

                    docRef.update("image", temp , "contents" , contents , "title" , title)
                }
            }

        }

        finish()
    }

}