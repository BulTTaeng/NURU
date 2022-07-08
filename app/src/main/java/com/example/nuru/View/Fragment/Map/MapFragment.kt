package com.example.nuru.View.Fragment.Map

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nuru.View.Activity.Map.MapsActivity
import com.example.nuru.R
import com.example.nuru.View.Activity.Map.SearchAddressActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() , OnMapReadyCallback {
    private var mapFragment: SupportMapFragment? = null
    lateinit var gso : GoogleSignInOptions
    private lateinit var mMap: GoogleMap
    private lateinit var locationCallback: LocationCallback // 위칫값 요청에 대한 갱신 정보를 받아옴
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위칫값 사용

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

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_CurrentLocation1.setOnClickListener{
            mMap?.let{
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                updateLocation()
            }
        }

        btn_Search1.setOnClickListener {
            val intent_address = Intent(requireContext(), SearchAddressActivity::class.java)
            intent_address.putExtra("ADDRESS", et_SearchAddress1.text.toString())
            startActivity(intent_address)
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val posLatLng = LatLng(
            35.1740336,
            126.9016885
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posLatLng ,
            MapsActivity.CAMERA_ZOOM_LEVEL
        ))
        widget_ProgressBar1.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(mapFragment != null){
            childFragmentManager.beginTransaction().remove(mapFragment!!).commitAllowingStateLoss()
        }
    }

}