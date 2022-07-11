package com.example.nuru.view.activity.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.nuru.model.data.farm.Farm

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_maps.*
import com.example.nuru.model.data.tmap.SearchResultEntity
import com.example.nuru.R
import com.example.nuru.utility.GetCurrentContext
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MapsActivity : AppCompatActivity(), OnMapReadyCallback , CoroutineScope {

    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var mapsActivity: MapsActivity

    var singletonC = GetCurrentContext.getInstance()

    //database connection
    private lateinit var database: DatabaseReference

    lateinit var user_farm_info : ArrayList<Farm>

    val db = FirebaseFirestore.getInstance()

    private lateinit var mMap: GoogleMap
    // 현재 위치를 검색하기 위함
    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위칫값 사용
    private lateinit var locationCallback: LocationCallback // 위칫값 요청에 대한 갱신 정보를 받아옴

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY: String = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 2021
    }

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

   // lateinit var binding: MapsActivity

    private var currentSelectMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        singletonC.setcurrentContext(this)

        widget_ProgressBar.visibility = View.GONE

        // 사용할 권한 array로 저장
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

        requirePermissions(permissions, 999) //권환 요쳥, 999는 임의의 숫자

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        database = Firebase.database.reference

        job = Job()

        user_farm_info =ArrayList<Farm>()

    }

    fun btnCurrentLocation(view : View){
        mMap?.let{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            updateLocation()
        }
    }

    fun btnSearch(view : View){
        val intent_address = Intent(this, SearchAddressActivity::class.java)
        intent_address.putExtra("ADDRESS", et_SearchAddress.text.toString())
        startActivity(intent_address)
    }

    fun returnMaps(): MapsActivity {
        return this
    }

    fun startProcess() {
        // SupportMapFragment를 가져와서 지도가 준비되면 알림을 받습니다.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /** 권한 요청*/
    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        } else {
            val isAllPermissionsGranted = permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            if (isAllPermissionsGranted) {
                permissionGranted(requestCode)
            } else {
                ActivityCompat.requestPermissions(this, permissions, requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissionDenied(requestCode)
            //여기 반대로 되야 작동함 , 이유를 모르겠음...
        } else {
            permissionGranted(requestCode)
        }
    }

    // 권한이 있는 경우 실행
    fun permissionGranted(requestCode: Int) {
        startProcess() // 권한이 있는 경우 구글 지도를준비하는 코드 실행
    }

    // 권한이 없는 경우 실행
    fun permissionDenied(requestCode: Int) {
        Toast.makeText(this
            , getString(R.string.authority_required)
            , Toast.LENGTH_LONG)
            .show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var searchResult: SearchResultEntity? = intent.getParcelableExtra<SearchResultEntity>(
            SEARCH_RESULT_EXTRA_KEY
        )

        if (searchResult != null) {
            val positionLatLng = LatLng(
                searchResult.locationLatLng.latitude.toDouble(),
                searchResult.locationLatLng.longitude.toDouble()
            )

            // 구글맵 마커 객체 설정
            val markerOptions = MarkerOptions().apply {
                position(positionLatLng)
                title(searchResult.name)
                snippet(searchResult.fullAddress)
            }

            // 카메라 줌 설정
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))


            currentSelectMarker = mMap.addMarker(markerOptions)!!
            currentSelectMarker?.showInfoWindow()
        }
        else{
            val posLatLng = LatLng(
                35.1740336,
                126.9016885
            )

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posLatLng , CAMERA_ZOOM_LEVEL))
        }
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //updateLocation()
    }

    // 위치 정보를 받아오는 역할
    @SuppressLint("MissingPermission")
    //requestLocationUpdates는 권한 처리가 필요한데 현재 코드에서는 확인 할 수 없음. 따라서 해당 코드를 체크하지 않아도 됨.
    fun updateLocation() {
        widget_ProgressBar.visibility = View.VISIBLE
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
        widget_ProgressBar.visibility = View.GONE
    }

    /*private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
            )
        }
    }*/


}