package com.example.nuru.view.activity.adminfarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nuru.R
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_farm.*

class AdminFarmActivity : AppCompatActivity() {

    lateinit var adminFarmController : NavController
    lateinit var navHostFragment: NavHostFragment
    lateinit var viewModel : MyFarmViewModel

    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_farm)

        val UserId = firebaseAuth.currentUser?.uid
        val docRef = db.collection("user").document(UserId.toString())
        viewModel = ViewModelProvider(this, ViewModelFactoryForMyFarm(docRef))
            .get(MyFarmViewModel::class.java)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_admin_farm_container) as NavHostFragment
        adminFarmController = navHostFragment.navController
        //adminFarmController = nav_admin_farm_container.findNavController()
        //Fragment container을 사용할때는 이렇게 찾아줘야 함
    }


}