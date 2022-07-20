package com.example.nuru.view.fragment.fitcheck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.nuru.R
import com.example.nuru.databinding.FragmentFitCheckSelectBinding
import com.example.nuru.databinding.FragmentStatusCheckReadyBinding
import com.example.nuru.viewmodel.fitcheck.FitCheckViewModel


class FitCheckSelectFragment : Fragment() {

    lateinit var binding: FragmentFitCheckSelectBinding
    lateinit var fitController : NavController
    val fitCheckViewModel : FitCheckViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_fit_check_select, container, false)
        binding.fragment = this@FitCheckSelectFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fitController = Navigation.findNavController(view)
        binding.viewModel = fitCheckViewModel
        binding.lifecycleOwner =this
    }

    fun cardAddressSelect(view : View){
        fitController.navigate(FitCheckSelectFragmentDirections.actionFitCheckSelectFragmentToFitCheckAddressCityFragment(0))
    }

    fun cardProductSelect(view: View){
        fitController.navigate(FitCheckSelectFragmentDirections.actionFitCheckSelectFragmentToFitCheckAddressCityFragment(2))
    }

}