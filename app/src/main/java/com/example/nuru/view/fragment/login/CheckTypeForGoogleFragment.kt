package com.example.nuru.view.fragment.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.R
import com.example.nuru.databinding.FragmentCheckTypeForGoogleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_check_type_for_google.*

class CheckTypeForGoogleFragment : Fragment() {

    lateinit var loginActivity: LoginActivity
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    lateinit var args: com.example.nuru.view.fragment.login.CheckTypeForGoogleFragmentArgs
    private lateinit var callback: OnBackPressedCallback
    private lateinit var binding: FragmentCheckTypeForGoogleBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(loginActivity , R.string.back_impossible , Toast.LENGTH_SHORT).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_check_type_for_google, container, false)
        binding.fragment = this@CheckTypeForGoogleFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar_googleSignUp.visibility = View.GONE

        val arg by navArgs<com.example.nuru.view.fragment.login.CheckTypeForGoogleFragmentArgs>()
        args = arg

    }

    //만약 이 페이지에서 프래그먼트 중단 상황이 발생하면
    //auth에는 로그인이 되 있는데, firebase에는 정보가 없는 상황이 발생해서 앱이 팅김
    //따라서 onPause에서 임시적으로 firebase에 값을 넣어준다
    override fun onPause() {
        super.onPause()
        writeNewUserWithTaskListeners(args.signUpInfo!!.userId ,
            args.signUpInfo!!.name , args.signUpInfo!!.email ,
            args.signUpInfo!!.type , toggle_farmer.isChecked ,
            toggle_admin.isChecked)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    fun toggleFarmer(view: View){
        Log.d("toggle_farmer" , toggle_farmer.isChecked.toString())
    }

    fun toggleAdmin(view: View){
        Log.d("toggle_admin" , toggle_admin.isChecked.toString())
    }

    fun btnSignUpGoogle(view: View){
        progressBar_googleSignUp.visibility = View.VISIBLE
        if(!toggle_admin.isChecked && !toggle_farmer.isChecked){
            Toast.makeText(loginActivity , R.string.select_admin_farm , Toast.LENGTH_LONG).show()
            progressBar_googleSignUp.visibility = View.GONE
        }
        else{
            writeNewUserWithTaskListeners(args.signUpInfo!!.userId ,
                args.signUpInfo!!.name , args.signUpInfo!!.email ,
                args.signUpInfo!!.type , toggle_farmer.isChecked ,
                toggle_admin.isChecked)
            Log.d("aaaaaaaaaaaaa" , args.signUpInfo!!.userId)
        }
    }

    private fun writeNewUserWithTaskListeners(userId: String, name: String, email: String, type : String, isFarmer : Boolean, isAdmin : Boolean) {

            val str = ArrayList<String>()
            str.add("zerozero")

            val data = hashMapOf(
                "name" to name,
                "userId" to userId,
                "email" to email,
                "type" to type,
                "farmList" to str,
                "isAdmin" to isAdmin,
                "isFarmer" to isFarmer
            )

            db.collection("user").document(userId)
                .set(data).addOnCompleteListener{
                    if(it.isSuccessful){
                        Log.d("success" , "ssssssssssss")
                        val ass = Intent(context, MyPageActivity::class.java)
                        startActivity(ass)
                        //LoginController.navigate(R.id.action_loginFragment2_to_myPageActivity)
                        activity?.finish()
                    }
                    else{
                        Log.d("ffffffffff" , "fffffffffffffffffffff")
                        progressBar_googleSignUp.visibility = View.GONE

                    }
                }
        //.addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        //.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }
}