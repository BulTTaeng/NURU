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
import com.example.nuru.view.activity.mypage.AddFarmActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_page.*
import kotlinx.android.synthetic.main.fragment_my_page.mypage_recycleView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MyPageFragment : Fragment() , CoroutineScope {

    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var mypageActivity: MyPageActivity

    val db = FirebaseFirestore.getInstance()

    lateinit var username : String

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
        //binding = DataBindingUtil.setContentView(this ,R.layout.fragment_my_page)
        //binding = FragmentMyPageBinding.inflate(layoutInflater)
        //binding.fragment = this@MyPageFragment

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
        /*if (UserId != null) {

            val docRef = db.collection("user").document(UserId)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("error", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {

                    user_farm_info =ArrayList<Farm>()
                    findFarmInfo(UserId)


                } else {
                    Log.d("current null", "Current data: null")
                }
            }

        }*/

        val UserId = firebaseAuth.currentUser?.uid
        val docRef = db.collection("user").document(UserId.toString())
        viewModel = ViewModelProvider(this, ViewModelFactoryForMyFarm(docRef))
            .get(MyFarmViewModel::class.java)


        docRef.get().addOnSuccessListener {
            username = it["name"].toString() + "\n" + it["email"].toString()
            txt_UserNameMyPage.text = username
        }

        Glide.with(this).load(getString(R.string.basic_image))
            .transform(CenterCrop(), RoundedCorners(100)).into(img_MyPageImage)

        // TODO : 이건 binding으로 해결 못함?
        swipe_in_mypage.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                val update = async {  viewModel.updateFarm() }
                update.await()
            }
        }
        adapter = FarmAdapter(myPageActivity)
        // Setting the Adapter with the recyclerview
        mypage_recycleView.layoutManager = LinearLayoutManager(myPageActivity)
        mypage_recycleView.adapter = adapter
        observeData()
    }

    fun observeData(){
        Log.d("[MyPageFragment]", "ovserve data 안에 들어옴!")
        viewModel.fetchData().observe(
            viewLifecycleOwner, Observer {
                widget_ProgressBarInMyPage.visibility = View.VISIBLE

                adapter.submitList(it.map{
                    it.copy()
                }
                ).let {
                    swipe_in_mypage.isRefreshing = false
                    widget_ProgressBarInMyPage.visibility = View.GONE
                }
            }
        )
    }


    private fun revokeAccess() { //회원탈퇴
        var login_type : String
        db.collection("user").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
            login_type = it["type"].toString()

            if(login_type.equals("email_login")){
                firebaseAuth.signOut()
            }
            else if(login_type.equals("google_login")){
                // Firebase sign out
                googleSignInClient.revokeAccess().addOnCompleteListener(mypageActivity) {
                    if(it.isSuccessful){
                        Toast.makeText(mypageActivity, getString(R.string.do_logout), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(mypageActivity, getString(R.string.logout_exception), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun btnCalNum(view : View) {
        Log.d("[Tag] : btn_cal_num", "특화계수 버튼 클릭!")
    }

    fun btnSetting(view : View) {
        Log.d("[Tag] : btn_setting", "셋팅 버튼 클릭")
        findNavController().navigate(R.id.action_myPageFragment_to_settingFragment)
    }

    fun btnAddFarm(view : View) {
        Log.d("[Tag] : btn_add_farm", "농장 추가 버튼 클릭")
        val intent = Intent(activity , AddFarmActivity::class.java)
        startActivity(intent)
    }

    /*private fun findFarmInfo(UserId : String){
        launch(coroutineContext) {
            try {
                //binding.widgetProgressBarInMyPage.isVisible = true // 로딩 표시
                widget_ProgressBarInMyPage.visibility = view.VISIBLE
                var i = 1
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    Info_user = db.collection("user").document(UserId.toString())
                    Info_user.get().addOnSuccessListener { document ->
                        if (document != null) {

                            username = document["name"] as String
                            var userEmail = document["email"] as String
                            user_farm_list = document["farmList"] as ArrayList<String>
                            //Log.d("userName", "DocumentSnapshot data: ${username} ")
                            txt_UserNameMyPage.text = username + "\n" +userEmail
                            //Log.d("userName", "DocumentSnapshot data: ${document.data}")
                            if(user_farm_list.isEmpty()){
                                user_farm_list.add("zerozero")
                            }
                            val Farm_Info = db.collection("farmList")

                            Farm_Info.whereIn("farmId", user_farm_list).get().addOnSuccessListener { documents ->
                                for(document in documents){

                                    val lati = document.data["latitude"] as Double
                                    val longti = document.data["longitude"] as Double

                                    val location = LatLng(lati, longti)

                                    user_farm_info.add(
                                        Farm(document.id, document.data["farmName"].toString() ,
                                            document.data["farmPhoto"] as ArrayList<String>,
                                            location, document.data["farmAddress"].toString() ,
                                            document.data["farmOwner"].toString(), i  , document.data["products"].toString()) )
                                    i += 1

                                }
                                val adapter = FarmAdapter(user_farm_info , mypageActivity)
                                // Setting the Adapter with the recyclerview
                                mypage_recycleView.layoutManager = LinearLayoutManager(mypageActivity)




                                adapter.setSearchResultList(user_farm_info) {

                                    val Intent = Intent(mypageActivity, MyFarmActivity::class.java)
                                    Intent.putExtra("FARMID",it.farm_id)
                                    startActivity(Intent)
                                }
                                adapter.currentPage = i
                                // Setting the Adapter with the recyclerview

                                mypage_recycleView.adapter = adapter
                            }

                        } else {
                            Log.d("userName", "No such document")
                        }
                    }.addOnFailureListener { exception ->
                        Log.d("UserName", "get failed with ", exception)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // error 해결 방법
                // Permission denied (missing INTERNET permission?) 인터넷 권한 필요
                // 또는 앱 삭제 후 재설치
            } finally {
                //binding.widgetProgressBarInMyPage.isVisible = false // 로딩 표시 완료 그런데 안됨 그래서 그냥 visibility로 먹임
                widget_ProgressBarInMyPage.visibility = view.GONE
            }
            //binding.widgetProgressBarInMyPage.isVisible = false
        }
    }*/



}