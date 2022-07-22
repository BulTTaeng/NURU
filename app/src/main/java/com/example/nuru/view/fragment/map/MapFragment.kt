package com.example.nuru.view.fragment.map

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nuru.R
import com.example.nuru.databinding.FragmentMapBinding
import com.example.nuru.view.activity.map.MapsActivity
import com.example.nuru.view.activity.map.SearchAddressActivity
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForMyFarm
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : Fragment() , OnMapReadyCallback {

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    private var mapFragment: SupportMapFragment? = null
    lateinit var gso : GoogleSignInOptions
    private lateinit var mMap: GoogleMap
    private lateinit var locationCallback: LocationCallback // 위칫값 요청에 대한 갱신 정보를 받아옴
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위칫값 사용
    private lateinit var binding: FragmentMapBinding



    val docRef = db.collection("user").document(firebaseAuth.currentUser?.uid.toString())
    val viewModel : MyFarmViewModel by viewModels{ ViewModelFactoryForMyFarm(docRef) }

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY: String = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 2021
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(mapFragment == null){
            mapFragment = SupportMapFragment.newInstance()
            mapFragment!!.getMapAsync(this)
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map1, mapFragment!!).commit()
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_map, container, false)
        binding.fragment = this@MapFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateFarm()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val posLatLng = LatLng(
            35.1740336,
            126.9016885
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posLatLng ,
            MapsActivity.CAMERA_ZOOM_LEVEL
        ))

        val xx = viewModel.fetchData().value


        xx!!.forEach {
            val farmMarker = mMap.addMarker(MarkerOptions().position(it.farm_latLng))
            farmMarker!!.tag = it.products
            farmMarker.title = it.farm_name
            val circleMaker = CircleOptions()
            circleMaker.center(it.farm_latLng)
            circleMaker.fillColor(R.color.light_green)
            circleMaker.radius(1000.0)
            //TODO:: stroke 색 뭘로하지?
            circleMaker.strokeColor(R.color.white)
            circleMaker.strokeWidth(4.0f)
            mMap.addCircle(circleMaker)
        }

        widget_ProgressBar1.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(mapFragment != null){
            childFragmentManager.beginTransaction().remove(mapFragment!!).commitAllowingStateLoss()
        }
    }

    fun btnSearch(view : View){
        val intent_address = Intent(requireContext(), SearchAddressActivity::class.java)
        intent_address.putExtra("ADDRESS", et_SearchAddress1.text.toString())
        startActivity(intent_address)
    }

    fun btnCurrentLocation1(view: View){
        mMap?.let{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            updateLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocation() {
        widget_ProgressBar1.visibility = View.VISIBLE
        val locationRequest = LocationRequest.create()

        /*locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }*/
        //이거 활성화 시키면 1초마다 제제리로 돌아감

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for(location in it.locations) {
                        setLastLocation(location)
                    }
                }

            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    fun setLastLocation(lastLocation: Location) {
        val LATLNG = LatLng(lastLocation.latitude, lastLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("Here!")

        val cameraPosition = CameraPosition.Builder()
            .target(LATLNG)
            .zoom(15.0f)
            .build()
        mMap.clear()
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        widget_ProgressBar1.visibility = View.GONE
    }

    /*fun DrawCircle(gMap: GoogleMap?) {
        val circle: Circle
        val CircleMaker = CircleOptions()
        val latlong = LatLng(13.0291, 80.2083) //Location
        CircleMaker.InvokeCenter(latlong) //
        CircleMaker.InvokeRadius(1000) //Radius in Circle
        CircleMaker.InvokeStrokeWidth(4)
        CircleMaker.InvokeStrokeColor(Android.Graphics.Color.ParseColor("#e6d9534f")) //Circle Color
        CircleMaker.InvokeFillColor(Color.Argb(28, 209, 72, 54))
        val camera: CameraUpdate = CameraUpdateFactory.NewLatLngZoom(latlong, 15) //Map Zoom Level
        circle = GMap.AddCircle(CircleMaker) //Gmap Add Circle
    }*/

}