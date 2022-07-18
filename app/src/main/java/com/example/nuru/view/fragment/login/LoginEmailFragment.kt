package com.example.nuru.view.fragment.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.nuru.R
import com.example.nuru.databinding.FragmentLoginBinding
import com.example.nuru.databinding.FragmentLoginEmailBinding
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.example.nuru.viewmodel.login.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_login_email.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginEmailFragment : Fragment() {
    lateinit var LoginController : NavController

    lateinit var loginActivity: LoginActivity

    private lateinit var binding: FragmentLoginEmailBinding

    private val userViewModel : UserViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoginController = login_navigation.findNavController()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    LoginController.navigate(R.id.action_loginEmailFragment_to_loginFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_email, container, false)
        binding.fragment = this@LoginEmailFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar_loginEmail.visibility = View.GONE
    }

    // Email SignIn
    fun btnLogIn(view:View){
        progressBar_loginEmail.visibility = View.VISIBLE
        btn_logIn.isEnabled=false
        var id :String = edt_email.text.toString()
        var pass : String = edt_password.text.toString()

        if(id == ""){
            Toast.makeText(loginActivity, getString(R.string.give_id), Toast.LENGTH_SHORT).show()
            btn_logIn.isEnabled=true
            progressBar_loginEmail.visibility = View.GONE
        }
        else if(pass == ""){
            Toast.makeText(loginActivity, getString(R.string.give_password), Toast.LENGTH_SHORT).show()
            btn_logIn.isEnabled=true
            progressBar_loginEmail.visibility = View.GONE
        }
        else {
            var loginCheck : Boolean = false
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    loginCheck = userViewModel.emailSignIn(id, pass, loginActivity)
                }.join()
                when (loginCheck) {
                    true -> {
                        progressBar_loginEmail.visibility = View.GONE
                        val ass = Intent(context, MyPageActivity::class.java)
                        startActivity(ass)
                        activity?.finish()
                    }
                    false -> {
                        progressBar_loginEmail.visibility = View.GONE
                        Toast.makeText(loginActivity, getString(R.string.id_password_wrong), Toast.LENGTH_SHORT).show()
                        btn_logIn.isEnabled=true
                    }
                }
            }
        }
    }

    fun btnToSignUp(view:View){
        LoginController.navigate(R.id.action_loginEmailFragment_to_signupFragment)
    }

}