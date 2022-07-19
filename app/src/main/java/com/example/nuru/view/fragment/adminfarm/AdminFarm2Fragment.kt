package com.example.nuru.view.fragment.adminfarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nuru.R
import com.example.nuru.databinding.FragmentAdminFarm2Binding
import com.example.nuru.databinding.FragmentAdminFarmPageBinding
import com.example.nuru.view.fragment.login.CheckTypeForGoogleFragmentArgs
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_farm.*
import kotlinx.android.synthetic.main.fragment_admin_farm2.*

class AdminFarm2Fragment : Fragment() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    lateinit var binding: FragmentAdminFarm2Binding
    val UserId = firebaseAuth.currentUser?.uid
    val docRef = db.collection("user").document(UserId.toString())
    private val viewModel : MyFarmViewModel by activityViewModels{ ViewModelFactoryForMyFarm(docRef) }
    lateinit var adminNavController: NavController

    lateinit var args: AdminFarm2FragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_farm2, container, false)
        binding.fragment = this@AdminFarm2Fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arg by navArgs<AdminFarm2FragmentArgs>()
        args = arg
        adminNavController = Navigation.findNavController(view)
        //txt_locationAdminFarm.text = args.loc.toString()

        txt_productsAdminFarm.text = viewModel.fetchData().value!!.get(args.loc!!).products
        txt_locationAdminFarm.text = viewModel.fetchData().value!!.get(args.loc!!).farm_address
    }

    fun btnToMyProduct(view:View){
        adminNavController.navigate(AdminFarm2FragmentDirections.actionAdminFarm2FragmentToAdminFarmSensorFragment(args.loc))
    }
}