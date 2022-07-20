package com.example.nuru.view.fragment.mainBusiness

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.R
import com.example.nuru.view.activity.mainbusiness.MainBusinessActivity
import com.example.nuru.view.adapter.MainBusinessAdapter
import com.example.nuru.viewmodel.mainbusiness.MainBusinessViewModel
import kotlinx.android.synthetic.main.fragment_main_business1.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainBusiness1Fragment : Fragment() {

    lateinit var adapter: MainBusinessAdapter
    private val mainBusinessViewModel : MainBusinessViewModel by activityViewModels()
    lateinit var mainBusinessActivity: MainBusinessActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainBusinessActivity = context as MainBusinessActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_business1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar_mainBusiness1.visibility = View.GONE

        adapter = MainBusinessAdapter(mainBusinessActivity)
        recycler_mainBusiness.adapter = adapter
        recycler_mainBusiness.layoutManager = LinearLayoutManager(mainBusinessActivity)

        CoroutineScope(Dispatchers.Main).launch {
            progressBar_mainBusiness1.visibility = View.VISIBLE
            var success = false
            CoroutineScope(Dispatchers.IO).launch {
                success = mainBusinessViewModel.getInfo()
            }.join()

            if(success){
                adapter.submitList(mainBusinessViewModel.fetchData().value)
            }
            else{
                Toast.makeText(mainBusinessActivity , getString(R.string.try_later) , Toast.LENGTH_LONG).show()
            }

            progressBar_mainBusiness1.visibility = View.GONE

        }

    }

}