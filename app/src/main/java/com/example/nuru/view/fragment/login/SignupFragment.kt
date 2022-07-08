package com.example.nuru.view.fragment.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.nuru.view.activity.login.LoginActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    //firebase Auth
    private lateinit var auth: FirebaseAuth

    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var LoginController : NavController

    lateinit var loginActivity: LoginActivity

    private lateinit var database: DatabaseReference

    val db = FirebaseFirestore.getInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar_Signup.visibility = View.GONE

        LoginController = login_navigation.findNavController()

        database = Firebase.database.reference
        auth = Firebase.auth

        var isAdmin : Boolean = false
        var isFarmer : Boolean =false

        chk_Admin.setOnCheckedChangeListener{compoundButton, b ->
            isAdmin = b
        }

        chk_Farmer.setOnCheckedChangeListener{compoundButton, b ->
            isFarmer = b
        }


        btnSignup.setOnClickListener {
            var email = etSignupEmail.text.toString()
            var pass = etSignupPassword.text.toString()
            var pass2 = etSignupPassword2.text.toString()
            var name = etSignupName.text.toString()
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

                progressBar_Signup.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    auth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(loginActivity){
                        if(it.isSuccessful){
                            async{registerPushToken()}
                            val user = auth?.currentUser?.uid
                            writeNewUserWithTaskListeners(user.toString(), name, email , getString(R.string.email_login) , isAdmin , isFarmer)
                            Toast.makeText(loginActivity, getString(R.string.singup_success), Toast.LENGTH_SHORT).show()
                            val ass = Intent(context, MyPageActivity::class.java)
                            startActivity(ass)
                            //LoginController.navigate(R.id.action_loginFragment2_to_myPageActivity)
                            activity?.finish()
                        }
                        else{
                            Toast.makeText(loginActivity, getString(R.string.singup_exception), Toast.LENGTH_SHORT).show()
                        }

                        progressBar_Signup.visibility = View.GONE
                    }
                }

            }
        }//end of btn setonclick listener
    }

    suspend fun registerPushToken() {
        //v17.0.0 이전까지는
        ////var pushToken = FirebaseInstanceId.getInstance().token
        //v17.0.1 이후부터는 onTokenRefresh()-depriciated
        //var pushToken: String? = null


        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        //var map = mutableMapOf<String, Any>()
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                var pushToken = task.result

                val data = hashMapOf(
                    "pushtoken" to pushToken,
                )
                db.collection("pushtokens").document(uid!!).set(data)
            }
            //pushToken = task.token
        }
    }

    fun writeNewUserWithTaskListeners(userId: String, name: String, email: String , type : String , isAdmin : Boolean , isFarmer : Boolean) {

        var temp : ArrayList<String>
        temp = ArrayList<String>()
        val data = hashMapOf(
            "name" to name,
            "userId" to userId,
            "email" to email,
            "type" to type,
            "farmList" to temp,
            "isAdmin" to isAdmin,
            "isFarmer" to isFarmer
        )

        db.collection("user").document(userId)
            .set(data)
            //.addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            //.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

}