package com.example.nuru.view.fragment.adminfarm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.R
import com.example.nuru.databinding.FragmentAdminFarm2Binding
import com.example.nuru.view.activity.adminfarm.AdminFarmActivity
import com.example.nuru.view.adapter.SensorImageAdapter
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_admin_farm_sensor.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminFarmSensorFragment : Fragment() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    lateinit var binding: FragmentAdminFarm2Binding
    val UserId = firebaseAuth.currentUser?.uid
    val docRef = db.collection("user").document(UserId.toString())
    private val viewModel : MyFarmViewModel by activityViewModels{ ViewModelFactoryForMyFarm(docRef) }

    lateinit var args: AdminFarmSensorFragmentArgs
    lateinit var adapter : SensorImageAdapter
    lateinit var adminFarmActivity: AdminFarmActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adminFarmActivity = context as AdminFarmActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_farm_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg by navArgs<AdminFarmSensorFragmentArgs>()
        args = arg

        val farmId = viewModel.fetchData().value!!.get(args.loc).farm_id

        CoroutineScope(Dispatchers.IO).launch {
            launch { viewModel.updateImage(farmId) }.join()
        }

        adapter = SensorImageAdapter(adminFarmActivity)
        recycler_adminFarmSensor.adapter = adapter
        recycler_adminFarmSensor.layoutManager = LinearLayoutManager(adminFarmActivity)
        adapter.submitList(viewModel.fetchImages().value)

        swipe_in_adminSensorImage.setOnRefreshListener {

            CoroutineScope(Dispatchers.IO).launch {
                launch { viewModel.updateImage(farmId) }.join()
            }
        }

        observeData()

    }

    fun observeData(){
        viewModel.fetchImages().observe(
            viewLifecycleOwner, androidx.lifecycle.Observer { list ->
                //TODO: 여기 왜 안되지?
                adapter.submitList(list.toMutableList())
//                adapter.submitList(list.map{
//                    it.copy()
//                }
//                )

                swipe_in_adminSensorImage.isRefreshing = false
            }
        )
    }

}