package com.example.nuru.view.activity.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.databinding.ActivityAddAdminBinding
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_admin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddAdminActivity : AppCompatActivity() {

    lateinit var farm_id: String
    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()
    private lateinit var binding: ActivityAddAdminBinding

    val firebaseAuth = FirebaseAuth.getInstance()

    val docRef = db.collection("user").document(firebaseAuth.currentUser?.uid.toString())

    val viewModel : MyFarmViewModel by viewModels{ ViewModelFactoryForMyFarm(docRef) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_admin)
        binding.activity = this@AddAdminActivity

        singletonC.setcurrentContext(this)
        val Intent = intent
        farm_id = Intent.getStringExtra("FARMID").toString()
        progressBar_addAdmin.visibility = View.GONE


        /*btn_addAdmin.setOnClickListener {
            val docRef = db.collection("farmList").document(farm_id)
            docRef.update("farmAdmin" , FieldValue.arrayUnion(edt_addminId.text.toString()))
            finish()

            //admin 아이디에서도 자기가 관리 하는 농장이 보이도록 해야 하나?
            //admin 과 농장 주인 사이에 관리 옵션이 뭐가 뭐가 달라야 하지?
        }*/
    }

    fun btnAddAdmin(view : View){
        CoroutineScope(Dispatchers.Main).launch {
            progressBar_addAdmin.visibility = View.VISIBLE

            var success = false
            CoroutineScope(Dispatchers.IO).launch {
                success = viewModel.addAdmin(edt_addminId.text.toString() , farm_id)
            }.join()

            if(success){
                finish()
            }else {
                Toast.makeText(getContext() , getString(R.string.try_later) , Toast.LENGTH_LONG).show()
                progressBar_addAdmin.visibility = View.GONE
            }
        }
    }

    fun getContext() : AddAdminActivity{
        return this
    }
}