package com.example.nuru.view.fragment.adminfarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.nuru.R
import com.example.nuru.databinding.FragmentAdminFarmPageBinding
import com.example.nuru.view.activity.adminfarm.AdminFarmActivity
import com.example.nuru.view.activity.mypage.AddFarmActivity
import com.example.nuru.view.adapter.FarmAdapter
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_farm.*
import kotlinx.android.synthetic.main.fragment_admin_farm_page.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AdminFarmPageFragment : Fragment() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val UserId = firebaseAuth.currentUser?.uid
    val docRef = db.collection("user").document(UserId.toString())

    private val viewModel : MyFarmViewModel by activityViewModels{ViewModelFactoryForMyFarm(docRef)}
    private lateinit var binding: FragmentAdminFarmPageBinding
    lateinit var adminFarmActivity: AdminFarmActivity
    private lateinit var adapter: FarmAdapter
    //lateinit var adminFarmController : NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adminFarmActivity = context as AdminFarmActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_farm_page, container, false)
        binding.fragment = this@AdminFarmPageFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //adminFarmController = Navigation.findNavController(view)
        val UserId = firebaseAuth.currentUser?.uid
        val docRef = db.collection("user").document(UserId.toString())

        var userText : String = ""
        CoroutineScope(Dispatchers.IO).launch {
            async {
                val nameEmail = viewModel.getUserNameAndEmail(UserId.toString())
                txt_UserNameAdminFarm.text = nameEmail[0]
                txt_UserEmailAdminFarm.text = nameEmail[1]
            }.await()
        }


        Glide.with(this).load(getString(R.string.basic_image))
            .transform(CenterCrop(), RoundedCorners(100)).into(img_adminFarmImage)

        swipe_in_adminFarm.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                async {  viewModel.updateFarm() }.await()
            }
        }
        adapter = FarmAdapter(adminFarmActivity , viewModel)
        // Setting the Adapter with the recyclerview
        mypage_recycleView.layoutManager = LinearLayoutManager(adminFarmActivity)
        mypage_recycleView.adapter = adapter
        observeData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RETURN_FROM_ADD) {
            val address = data?.getStringExtra("ADD_FARM_DONE")
            if (address == "OK") viewModel.updateFarm()
        }
    }

    fun observeData(){
        viewModel.fetchData().observe(
            viewLifecycleOwner, Observer {
                progressBar_adminFarm.visibility = View.VISIBLE

                adapter.submitList(it.map{
                    it.copy()
                }
                ).let {
                    swipe_in_adminFarm.isRefreshing = false
                    progressBar_adminFarm.visibility = View.GONE
                }
            }
        )
    }


    fun btnAddFarm(view : View) {
        Log.d("[Tag] : btn_add_farm", "농장 추가 버튼 클릭")
        val intent = Intent(activity , AddFarmActivity::class.java)
        startActivityForResult(intent , RETURN_FROM_ADD)
    }

    companion object {
        const val RETURN_FROM_ADD = 11
    }
}