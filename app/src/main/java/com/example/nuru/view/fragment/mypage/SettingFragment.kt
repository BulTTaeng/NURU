package com.example.nuru.view.fragment.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.*
import com.example.nuru.databinding.FragmentSettingBinding
import com.example.nuru.model.data.setting.SettingItem
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.view.adapter.SettingAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_setting.*
import androidx.core.content.ContextCompat.getSystemService

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import androidx.core.content.ContextCompat
import org.apache.poi.ss.formula.functions.T
import android.content.ComponentName

import android.os.IBinder

import android.content.ServiceConnection
import android.content.BroadcastReceiver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import com.example.nuru.repository.service.WithdrawalService


class SettingFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    lateinit var mypageActivity: MyPageActivity
    private lateinit var googleSignInClient: GoogleSignInClient
    var login_type : String = ""
    lateinit var uid : String
    private lateinit var binding : FragmentSettingBinding
    lateinit var myPageActivity : MyPageActivity

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msg = intent.getStringExtra("KEYVALUE")

            if(msg == "good"){
                Toast.makeText(mypageActivity, "good from service $msg", Toast.LENGTH_SHORT).show()
                endService()
                progressBar_setting.visibility = View.GONE
                val intent = Intent(getActivity(), LoginActivity::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(mypageActivity)
            }
            else if(msg =="error"){
                Toast.makeText(activity, "error from service $msg", Toast.LENGTH_SHORT).show()
                endService()
                progressBar_setting.visibility = View.GONE
            }
            else{
                Log.e("What is this " , "Something wrong with broadcasting receiving")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPageActivity = context as MyPageActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        mypageActivity = context as MyPageActivity
        uid = firebaseAuth.currentUser!!.uid

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(mypageActivity, gso)
        db.collection("user").document(firebaseAuth.currentUser!!.uid).get()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_setting, container, false)
        binding.fragment = this@SettingFragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar_setting.visibility = View.GONE

        // Setting the Adapter with the recyclerview
        val adapter = SettingAdapter(mypageActivity)
        binding.settingRecycleView.layoutManager = LinearLayoutManager(mypageActivity)
        binding.settingRecycleView.adapter = adapter

        adapter.data = listOf(
            SettingItem("회원탈퇴"),
            SettingItem("로그아웃")
        )

        adapter.currentPage = 2

        adapter.setOnClickListener(adapter.data) {
            if(it.title == getString(R.string.logout)){
                Log.d("[SettingFragment]","로그아웃을 누르셨습니다.")
                signOut()
            }
            else if(it.title == getString(R.string.withdrawal)){
                Log.d("[SettingFragment]","회원탈퇴를 누르셨습니다.")
                //revokeAccess()
                progressBar_setting.visibility = View.VISIBLE
                startService()

            }
        }
        adapter.notifyDataSetChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
        myPageActivity?.let { unRegister(it) }
    }

    fun register(ctx: Context) {
        LocalBroadcastManager.getInstance(ctx).registerReceiver(
            receiver, IntentFilter("DONE")
        )
    }

    fun unRegister(ctx: Context) {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(
            receiver
        )
    }

    fun startService()
    {
        val intent = Intent(myPageActivity, WithdrawalService::class.java)
        myPageActivity.startService(intent)
        myPageActivity.let{
            register(it)
        }
    }

    fun endService()
    {
        val intent = Intent(myPageActivity, WithdrawalService::class.java)
        myPageActivity.stopService(intent)
    }

    fun btnCancelSetting(view : View) {
        Log.d("[Tag] : 셋팅 취소", "셋팅 취소 버튼 클릭")
        findNavController().navigate(R.id.myPageFragment)
    }



    private fun signOut() { // 로그아웃
        progressBar_setting.visibility = View.VISIBLE
        db.collection("user").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
            login_type = it["type"].toString()

            if(login_type.equals("email_login")){
                firebaseAuth.signOut()
                progressBar_setting.visibility = View.GONE
                val intent = Intent(getActivity(), LoginActivity::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(mypageActivity)
            }
            else if(login_type.equals("google_login")){
                // Firebase sign out
                googleSignInClient.signOut().addOnCompleteListener{

                    if(it.isSuccessful){
                        Toast.makeText(mypageActivity, getString(R.string.do_logout), Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut()
                        progressBar_setting.visibility = View.GONE
                        val intent = Intent(getActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        ActivityCompat.finishAffinity(mypageActivity)
                    }
                    else{
                        Toast.makeText(mypageActivity, getString(R.string.logout_exception), Toast.LENGTH_SHORT).show()
                        progressBar_setting.visibility = View.GONE
                    }
                }
            }
        }
    }

}