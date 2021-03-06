package com.example.nuru.view.activity.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.R
import com.example.nuru.databinding.ActivityNewMyFarmBinding
import com.example.nuru.databinding.ActivityNewMyFarmBindingImpl
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_new_my_farm.*

class NewMyFarmActivity : AppCompatActivity() , OnMapReadyCallback {
    lateinit var gso : GoogleSignInOptions
    lateinit var myPageActivity: MyPageActivity
    private lateinit var mMap: GoogleMap
    //TODO :: share viewModel with MypageFragment?? 꼭 필요할까?
    private var mapFragment: SupportMapFragment? = null
    private lateinit var binding: ActivityNewMyFarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_my_farm)
        binding.activity = this@NewMyFarmActivity

        val x = getString(R.string.basic_image)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_inFrame) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intent: Intent = getIntent()

        binding.farm = intent.getParcelableExtra<Farm>("FARM")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnCameraMoveStartedListener {
            map_inMyPage.parent.requestDisallowInterceptTouchEvent(true)

        }

        mMap.setOnCameraIdleListener {
            map_inMyPage.parent.requestDisallowInterceptTouchEvent(false)

        }
        mMap.moveCamera(
            //CameraUpdateFactory.newLatLngZoom(args.farm!!.farm_latLng, 17f)
            CameraUpdateFactory.newLatLngZoom(binding.farm!!.farm_latLng, 17f)
        )

        //val farm_loc_maker = mMap.addMarker(MarkerOptions().position(args.farm!!.farm_latLng))
        val farm_loc_maker = mMap.addMarker(MarkerOptions().position(binding.farm!!.farm_latLng))
        farm_loc_maker!!.tag = getString(R.string.my_farm)

    }
}