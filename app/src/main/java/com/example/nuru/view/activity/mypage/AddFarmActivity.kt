package com.example.nuru.view.activity.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.databinding.ActivityAddFarmBinding
import com.example.nuru.model.data.farm.FarmEntity
import com.example.nuru.model.data.tmap.SearchResultEntity
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_farm.*
import kotlinx.coroutines.*

class AddFarmActivity : AppCompatActivity() {
    var latitude: Double = 0.0
    var longtitude: Double = 0.0

    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityAddFarmBinding

    //lateinit var viewModel : MyFarmViewModel

    val docRef = db.collection("user").document(firebaseAuth.currentUser?.uid.toString())

    val viewModel : MyFarmViewModel by viewModels{ViewModelFactoryForMyFarm(docRef)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_farm)`
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_farm)
        binding.activity = this@AddFarmActivity
        singletonC.setcurrentContext(this)
        binding.progressBarAddFarm.visibility = View.GONE

        binding.address = getString(R.string.press_address_find)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RETURN_FROM_SEARCH) {
            val address = data?.getParcelableExtra<SearchResultEntity>("SEARCH_RESULT_EXTRA_KEY")
            if (address != null) {
                latitude = address.locationLatLng.latitude.toDouble()
                longtitude = address.locationLatLng.longitude.toDouble()
                binding.address = address.fullAddress
            }
        }

    }

    fun btnFindAddress(view: View) {
        val Intent =
            Intent(this, com.example.nuru.view.activity.map.SearchAddressActivity2::class.java)
        startActivityForResult(Intent , RETURN_FROM_SEARCH)
        //finish()
        //MyPageController.navigate(R.id.action_addFarmFragment_to_myPageFragment)
    }

    fun btnAddFarmInfo(view: View) {
        binding.progressBarAddFarm.visibility = View.VISIBLE
        btn_AddFarmInfo.isEnabled = false
        var ok: Boolean = false

        val farmname = edt_farmName.text
        val products = edt_products.text
        val UserId = firebaseAuth.currentUser?.uid
        var temp = ArrayList<String>()
        temp.add(getString(R.string.empty_image))
        var t = ArrayList<String>()
        t.add(UserId.toString())

        val farmDao = FarmEntity(
            binding.address.toString(),
            "farmId",
            UserId.toString(),
            farmname.toString(),
            latitude.toDouble(),
            longtitude,
            products.toString(),
            temp,
            t
        )

        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).launch {
                ok = viewModel.addFarm(farmDao)
            }.join()

            Log.d("okkkkkkkkkkk",ok.toString())
            if (ok) {
                finish()
            } else {
                Log.d("error", "FirebaseWriting Failed")
                binding.progressBarAddFarm.visibility = View.GONE
                btn_AddFarmInfo.isEnabled = true
            }
        }
    }


    companion object{
        const val RETURN_FROM_SEARCH = 765
    }
}