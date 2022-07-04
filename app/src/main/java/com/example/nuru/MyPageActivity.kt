package com.example.nuru

import KeepStateNavigator
import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

import com.example.nuru.databinding.ActivityMyPageBinding
import com.example.nuru.observer.NetworkConnection
import com.example.nuru.utility.GetCurrentContext
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_my_page.*
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import java.math.BigDecimal
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController


class MyPageActivity : AppCompatActivity() {

    private lateinit var connection : NetworkConnection
    var singletonC = GetCurrentContext.getInstance()
    private var _binding : ActivityMyPageBinding ?= null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_my_page)
        _binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        singletonC.setcurrentContext(this)


        connection = NetworkConnection(applicationContext)
        connection.observeForever( Observer { isConnected ->
            if (!isConnected)
            {
                Log.d("INTERNET","INTERNET CONNECTION DISCONNECTED")

                AlertDialog.Builder(singletonC.getcurrentContext()!!)
                    .setTitle(getString(R.string.check_internet_connection))
                    .setPositiveButton(getString(R.string.go_back)) {
                            dialogInterface: DialogInterface, i: Int ->
                    } .setNegativeButton(getString(R.string.close_app)) {
                            dialogInterface: DialogInterface, i: Int -> exitApp()
                    } .show()
            }
        })

        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

        requirePermissions(permissions, 999)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController


        // KeepStateNavigator navController에 추가
        val navigator = KeepStateNavigator(this, navHostFragment.childFragmentManager, R.id.nav_host_fragment_container)
        navController.navigatorProvider.addNavigator(navigator)

        navController.setGraph(R.navigation.mypage_nav_graph)

        //btn_BottomNavigation_inMyPage.setupWithNavController(MyPageController)
        binding.bottomNavi.setupWithNavController(navController = navController)

        // Setup the ActionBar with navController and 3 top level destinations

        //readExcelFileFromAssets()
    }


    private fun readExcelFileFromAssets() {
        val myInput: InputStream
        val assetManager = assets
        //  엑셀 시트 열기
        myInput = assetManager.open("e.xlsx")
        //Instantiate Excel workbook using existing file:
        var xlWb = WorkbookFactory.create(myInput)

        //Row index specifies the row in the worksheet (starting at 0):
        val rowNumber = 0
        //Cell index specifies the column within the chosen row (starting at 0):
        val columnNumber = 0

        //Get reference to first sheet:
        val xlWs = xlWb.getSheetAt(0)
        val xx = xlWs.getRow(5).getCell(341).toString()
        val x = BigDecimal(xx).toPlainString()
    }


    fun returnMyPage(): MyPageActivity {
        return this
    }

    fun exitApp(){
        moveTaskToBack(true);						// 태스크를 백그라운드로 이동
        finishAndRemoveTask();						// 액티비티 종료 & 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
    }

    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //permissionGranted(requestCode)
        } else {
            val isAllPermissionsGranted = permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            if (isAllPermissionsGranted) {
                //permissionGranted(requestCode)
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
            //permissionDenied(requestCode)
            //여기 반대로 되야 작동함 , 이유를 모르겠음...
        } else {
            //permissionGranted(requestCode)
        }
    }



}