package com.example.nuru.view.fragment.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.nuru.*
import com.example.nuru.view.adapter.FarmAdapter
import com.example.nuru.databinding.FragmentMyPageBinding
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.view.activity.adminfarm.AdminFarmActivity
import com.example.nuru.view.activity.counsel.CounselActivity
import com.example.nuru.view.activity.mypage.AddFarmActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_page.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MyPageFragment : Fragment() , CoroutineScope {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client

    val db = FirebaseFirestore.getInstance()

    lateinit var user_farm_info : ArrayList<Farm>

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: FragmentMyPageBinding

    private lateinit var adapter: FarmAdapter

    lateinit var viewModel : MyFarmViewModel

    lateinit var myPageActivity : MyPageActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPageActivity = context as MyPageActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        job = Job()

        user_farm_info =ArrayList<Farm>()

        firebaseAuth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_page, container, false)
        binding.fragment = this@MyPageFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        val UserId = firebaseAuth.currentUser?.uid
        val docRef = db.collection("user").document(UserId.toString())
        viewModel = ViewModelProvider(this, ViewModelFactoryForMyFarm(docRef))
            .get(MyFarmViewModel::class.java)

        var userText : String = ""
        CoroutineScope(Dispatchers.IO).launch {
            async {
                val nameEmail = viewModel.getUserNameAndEmail(UserId.toString())
                txt_UserNameMyPage.text = nameEmail[0]
                txt_UserEmailMyPage.text = nameEmail[1]
            }.await()
        }

        Glide.with(this).load(getString(R.string.basic_image))
            .transform(CenterCrop(), RoundedCorners(100)).into(img_MyPageImage)
    }

    fun btnSetting(view : View) {
        Log.d("[Tag] : btn_setting", "셋팅 버튼 클릭")
        findNavController().navigate(R.id.action_myPageFragment_to_settingFragment)
    }

    fun btnCareMyFarm(view: View){
        startActivity(Intent(myPageActivity , AdminFarmActivity::class.java))
    }

    fun btnCounseling(view: View) {
        Log.d("전문가상담", "클릭")
        startActivity(Intent(myPageActivity , CounselActivity::class.java))
    }

}