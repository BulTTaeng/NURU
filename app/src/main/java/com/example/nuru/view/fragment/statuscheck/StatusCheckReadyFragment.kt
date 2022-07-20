package com.example.nuru.view.fragment.statuscheck

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.nuru.R
import com.example.nuru.databinding.FragmentStatusCheckReadyBinding
import com.example.nuru.model.data.statuscheck.ImageBitmap
import com.example.nuru.view.activity.statuscheck.StatusCheckActivity

class StatusCheckReadyFragment : Fragment() {

    lateinit var statusCheckActivity: StatusCheckActivity

    lateinit var binding : FragmentStatusCheckReadyBinding
    lateinit var statusCheckNavController: NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        statusCheckActivity = context as StatusCheckActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_status_check_ready, container, false)
        binding.fragment = this@StatusCheckReadyFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusCheckNavController = Navigation.findNavController(view)
    }

    fun btnCheck(view: View){
        if(checkPermission()){
            dispatchTakePictureIntent()
        }
        else{
            requestPermission()
            Toast.makeText(statusCheckActivity , getString(R.string.camera_ok_try) , Toast.LENGTH_LONG).show()
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode == Activity.RESULT_OK) {

            if( requestCode == REQUEST_IMAGE_CAPTURE)
            {
                val imageBitmap : Bitmap? = data?.extras?.get("data") as Bitmap

                if(imageBitmap != null) {

                    val image = ImageBitmap(imageBitmap)

                    statusCheckNavController.navigate(
                        StatusCheckReadyFragmentDirections.actionStatusCheckReadyFragmentToStatusCheckResultFragment(
                            image
                        )
                    )
                    //GImageView.setImageBitmap(imageBitmap)
                }

            }
        }
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if( requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(statusCheckActivity, "권한 설정 OK", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(statusCheckActivity, "권한 허용 안됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(statusCheckActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,CAMERA),1)

    }
    private fun checkPermission():Boolean{

        return (ContextCompat.checkSelfPermission(statusCheckActivity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(statusCheckActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    }

    private val REQUEST_IMAGE_CAPTURE = 2
    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//            }
//        }

        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE)
    }

}