package com.example.nuru.view.fragment.fitcheck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.nuru.R
import com.example.nuru.databinding.FragmentFitCheckResultBinding
import com.example.nuru.databinding.FragmentFitCheckSelectBinding
import com.example.nuru.viewmodel.fitcheck.FitCheckViewModel


class FitCheckResultFragment : Fragment() {

    val fitCheckViewModel : FitCheckViewModel by activityViewModels()
    lateinit var binding: FragmentFitCheckResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_fit_check_result, container, false)
        binding.fragment = this@FitCheckResultFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = fitCheckViewModel
        binding.lifecycleOwner =this

        //TODO:: select image with 특화 계수 value

    }

}