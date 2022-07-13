package com.example.nuru.view.fragment.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.R
import com.example.nuru.databinding.FragmentSignupBinding
import com.example.nuru.viewmodel.login.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    lateinit var LoginController : NavController

    lateinit var loginActivity: LoginActivity

    private lateinit var binding: FragmentSignupBinding

    private lateinit var userViewModel : UserViewModel

    val db = FirebaseFirestore.getInstance()

    var isAdmin : Boolean = false
    var isFarmer : Boolean =false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_signup, container, false)
        binding.fragment = this@SignupFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar_signup.visibility = View.GONE

        LoginController = login_navigation.findNavController()

        userViewModel = UserViewModel()
    }

    fun chkAdmin(view:View){
        isAdmin = !isAdmin
    }

    fun chkFarmer(view:View){
        isFarmer = !isFarmer
    }

    fun btnSignup(view:View){
        var email = edt_signupEmail.text.toString()
        var pass = edt_signupPassword.text.toString()
        var pass2 = edt_signupPassword2.text.toString()
        var name = edt_signupName.text.toString()
        if(email.isEmpty() || email.equals("")){
            Toast.makeText(loginActivity, getString(R.string.email), Toast.LENGTH_SHORT).show()
        }
        else if(!pass.equals(pass2)){
            Toast.makeText(loginActivity, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
        }
        else if(pass.length < 6){
            Toast.makeText(loginActivity, getString(R.string.password_longer_6), Toast.LENGTH_SHORT).show()
        }
        else if(name.isEmpty() || name.equals("")){
            Toast.makeText(loginActivity, getString(R.string.give_name), Toast.LENGTH_SHORT).show()
        }
        else if(!isAdmin && !isFarmer){
            Toast.makeText(loginActivity, getString(R.string.select_admin_farm), Toast.LENGTH_SHORT).show()
        }
        else{
            var loginResult : Boolean = false
            progressBar_signup.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    loginResult = userViewModel.registerUser(email, pass, loginActivity, name, isAdmin, isFarmer)
                }.join()
                if (loginResult) {
                    Toast.makeText(loginActivity, getString(R.string.singup_success), Toast.LENGTH_SHORT).show()
                    val ass = Intent(context, MyPageActivity::class.java)
                    startActivity(ass)
                    activity?.finish()
                    progressBar_signup.visibility = View.GONE
                }
                else {
                    Toast.makeText(loginActivity, getString(R.string.singup_exception), Toast.LENGTH_SHORT).show()
                    progressBar_signup.visibility = View.GONE
                }
            }
        }
    }
}