package com.example.nuru.View.Activity.Map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_maps2.*
import com.example.nuru.Model.Data.TMap.SearchResultEntity
import com.example.nuru.R
import com.example.nuru.Utility.GetCurrentContext
import com.example.nuru.View.Activity.MyPage.AddFarmActivity
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback , CoroutineScope {

    lateinit var mapsActivity: MapsActivity2
    var singletonC = GetCurrentContext.getInstance()

    //database connection
    private lateinit var database: DatabaseReference


    val db = FirebaseFirestore.getInstance()

    private lateinit var mMap: GoogleMap

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
        setContentView(R.layout.activity_maps2)
        singletonC.setcurrentContext(this)

        widget_ProgressBar2.visibility = View.GONE

        // 사용할 권한 array로 저장
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

        requirePermissions(permissions, 999) //권환 요쳥, 999는 임의의 숫자

        btn_ConfirmLocation.setOnClickListener {
            //val intent = Intent(this, MapsActivity::class.java)
            //startActivity(intent)

            var searchResult1: SearchResultEntity? = intent.getParcelableExtra<SearchResultEntity>(
                SEARCH_RESULT_EXTRA_KEY
            )

            val intent = Intent(this, AddFarmActivity::class.java)
            if(searchResult1 == null){
                Toast.makeText(this, getString(R.string.try_later) , Toast.LENGTH_LONG).show()
            }
            else{
                intent.putExtra("ADDRESS", searchResult1.fullAddress)
                val b = Bundle()
                b.putDouble("latitude", searchResult1.locationLatLng.latitude.toDouble())
                b.putDouble("longtitude" , searchResult1.locationLatLng.longitude.toDouble())
                intent.putExtras(b)
                val searchaddressactivity2 = SearchAddressActivity2()
                searchaddressactivity2.endSearchAddressActivity2()
                startActivity(intent)
                finish()
            }
        }

    }

    fun startProcess() {
        // SupportMapFragment를 가져와서 지도가 준비되면 알림을 받습니다.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
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
            permissionGranted(requestCode)
        } else {
            permissionDenied(requestCode)
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
        widget_ProgressBar2.visibility = View.GONE
    }


}