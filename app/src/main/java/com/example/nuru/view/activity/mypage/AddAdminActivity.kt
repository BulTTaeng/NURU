package com.example.nuru.view.activity.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.databinding.ActivityAddAdminBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_admin.*

class AddAdminActivity : AppCompatActivity() {

    lateinit var farm_id: String
    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()
    private lateinit var binding: ActivityAddAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_admin)
        binding.activity = this@AddAdminActivity

        singletonC.setcurrentContext(this)
        val Intent = intent
        farm_id = Intent.getStringExtra("FARMID").toString()

        /*btn_addAdmin.setOnClickListener {
            val docRef = db.collection("farmList").document(farm_id)
            docRef.update("farmAdmin" , FieldValue.arrayUnion(edt_addminId.text.toString()))
            finish()

            //admin 아이디에서도 자기가 관리 하는 농장이 보이도록 해야 하나?
            //admin 과 농장 주인 사이에 관리 옵션이 뭐가 뭐가 달라야 하지?
        }*/
    }

    fun btnAddAdmin(view : View){
        val docRef = db.collection("farmList").document(farm_id)
        docRef.update("farmAdmin" , FieldValue.arrayUnion(edt_addminId.text.toString()))
        finish()
    }
}