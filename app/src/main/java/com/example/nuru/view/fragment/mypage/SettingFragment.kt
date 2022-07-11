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

class SettingFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    lateinit var mypageActivity: MyPageActivity
    private lateinit var googleSignInClient: GoogleSignInClient
    var login_type : String = ""
    lateinit var uid : String
    private lateinit var binding : FragmentSettingBinding

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
                revokeAccess()
            }
        }
        adapter.notifyDataSetChanged()
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

    // TODO : 회원 탈퇴 로직 수정 (Github Issue #7)
    // Trigger가 없다면, 회원 삭제 버튼을 눌렀을 때, firebase DB에 정보들을 삭제하고 탈퇴해야함.
    fun revokeAccess() {
        progressBar_setting.visibility = View.VISIBLE
        val fAuth: FirebaseUser = firebaseAuth.getCurrentUser()!!
        db.collection("user").document(uid).delete().addOnCompleteListener{
            if(it.isSuccessful){
                Log.d("[revokeAccess]","uid collection 회원정보 delete 성공")
                fAuth!!.delete().addOnCompleteListener{
                    if(it.isSuccessful){
                        Log.d("[revokeAccess]","firebaseAuth 회원정보 delete 성공")
                        Toast.makeText(mypageActivity, R.string.success_withdrawal , Toast.LENGTH_SHORT).show()

                        progressBar_setting.visibility = View.GONE
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(getActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        ActivityCompat.finishAffinity(mypageActivity)
                    }
                    else{
                        Toast.makeText(mypageActivity, R.string.fail_withdrawal , Toast.LENGTH_SHORT).show()
                        progressBar_setting.visibility = View.GONE
                    }
                }
            }
            else{
                Toast.makeText(mypageActivity, R.string.fail_withdrawal , Toast.LENGTH_SHORT).show()
                progressBar_setting.visibility = View.GONE
            }
        }
    }
}