package com.example.nuru.view.fragment.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.*
import com.example.nuru.model.data.setting.SettingItem
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.view.adapter.SettingAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    lateinit var mypageActivity: MyPageActivity
    private lateinit var googleSignInClient: GoogleSignInClient
    var login_type : String = ""
    lateinit var uid : String


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar_setting.visibility = View.GONE
        val items = mutableListOf<SettingItem>()
        items.add(SettingItem( getString(R.string.withdrawal)))
        items.add(SettingItem( getString(R.string.logout)))
        val adapter = SettingAdapter(items)
        setting_recycleView.layoutManager = LinearLayoutManager(mypageActivity)
        adapter.currentPage = 2
        // Setting the Adapter with the recyclerview


        adapter.setSearchResultList(items) {
            if(it.title == getString(R.string.logout)){
                signOut()
            }
            else if(it.title == getString(R.string.withdrawal)){
                revokeAccess()
            }
        }

        setting_recycleView.adapter = adapter

        btn_Cancel_Setting.setOnClickListener {
            findNavController().navigate(R.id.myPageFragment)
        }
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

    fun revokeAccess() {

        progressBar_setting.visibility = View.VISIBLE
        db.collection("user").document(uid).delete().addOnCompleteListener{
            if(it.isSuccessful){
                firebaseAuth.getCurrentUser()!!.delete().addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(mypageActivity, R.string.success_withdrawal , Toast.LENGTH_SHORT).show()

                        progressBar_setting.visibility = View.GONE
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