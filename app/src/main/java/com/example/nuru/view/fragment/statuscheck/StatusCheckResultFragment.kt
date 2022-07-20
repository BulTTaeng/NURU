package com.example.nuru.view.fragment.statuscheck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.nuru.R
import com.example.nuru.view.fragment.adminfarm.AdminFarm2FragmentArgs
import com.example.nuru.view.fragment.login.CheckTypeForGoogleFragmentArgs
import kotlinx.android.synthetic.main.fragment_status_check_result.*


class StatusCheckResultFragment : Fragment() {

    lateinit var args: StatusCheckResultFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_check_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg by navArgs<StatusCheckResultFragmentArgs>()
        args = arg

        img_cameraImage.setImageBitmap(arg.image.imageBitmap)
    }

}