package com.example.nuru.view.fragment.fitcheck

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nuru.R
import com.example.nuru.databinding.FragmentFitCheckAddressCityBinding
import com.example.nuru.view.activity.fitcheck.FitCheckActivity
import com.example.nuru.view.adapter.CityProductAdapter
import com.example.nuru.view.fragment.adminfarm.AdminFarmSensorFragmentArgs
import com.example.nuru.view.fragment.statuscheck.StatusCheckResultFragmentArgs
import com.example.nuru.viewmodel.fitcheck.FitCheckViewModel
import kotlinx.android.synthetic.main.fragment_fit_check_address_city.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitCheckAddressProductFragment : Fragment() {

    lateinit var binding : FragmentFitCheckAddressCityBinding
    lateinit var fitCheckActivity: FitCheckActivity
    lateinit var adapter : CityProductAdapter
    lateinit var args: FitCheckAddressProductFragmentArgs
    val fitCheckViewModel : FitCheckViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fitCheckActivity = context as FitCheckActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_fit_check_address_city, container, false)
        binding.fragment = this@FitCheckAddressProductFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arg by navArgs<FitCheckAddressProductFragmentArgs>()
        args = arg
        adapter = CityProductAdapter(fitCheckActivity , args.check, fitCheckViewModel)

        CoroutineScope(Dispatchers.Main).launch {
            progressBar_fitCheckAddress.visibility = View.VISIBLE
            var success = false
            CoroutineScope(Dispatchers.IO).launch {
                when(args.check){
                    0 -> success = fitCheckViewModel.getCity()
                    2 -> success = fitCheckViewModel.getProduct()
                }

            }.join()

            if(success){
                when(args.check){
                    0 -> adapter.submitList(fitCheckViewModel.fetchCityData().value)
                    2 -> adapter.submitList(fitCheckViewModel.fetchProductData().value)
                }

                recycler_addressCity.adapter = adapter
                recycler_addressCity.layoutManager = GridLayoutManager(fitCheckActivity , 3)
            }
            else{
                Toast.makeText(context , getString(R.string.try_later) , Toast.LENGTH_LONG).show()
            }
            progressBar_fitCheckAddress.visibility = View.GONE
        }
    }


}