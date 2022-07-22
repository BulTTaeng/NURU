package com.example.nuru.view.fragment.alarm

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.R
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.view.adapter.TextAdapter
import com.example.nuru.viewmodel.alarm.AlarmViewModel
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


class AlarmFragment : Fragment() {

    val alarmViewModel by lazy { ViewModelProvider(this).get(AlarmViewModel::class.java) }
    lateinit var myPageActivity: MyPageActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPageActivity = context as MyPageActivity

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upateAlarm()

        swipe_in_alarm.setOnRefreshListener {
            upateAlarm()
        }


    }

    fun upateAlarm(){
        CoroutineScope(Dispatchers.Main).launch {

            val tasks = listOf(
                CoroutineScope(Dispatchers.IO).launch {
                    alarmViewModel.getFarmAlarm()
                },

                CoroutineScope(Dispatchers.IO).launch {
                    alarmViewModel.getCommunityAlarm()
                },

                CoroutineScope(Dispatchers.IO).launch {
                    alarmViewModel.getMainBusinessAlarm()
                }
            )

            tasks.joinAll()

            val adapterFarm = TextAdapter(myPageActivity)
            val adapterCommunity = TextAdapter(myPageActivity)
            val adapterMainBusiness = TextAdapter(myPageActivity)


            adapterFarm.data = alarmViewModel.fetchFarmData().value!!
            adapterCommunity.data = alarmViewModel.fetchCommunityData().value!!
            adapterMainBusiness.data = alarmViewModel.fetchMainBusinessData().value!!

            recycler_alarmFarm.adapter = adapterFarm
            recycler_alarmCommunity.adapter = adapterCommunity
            recycler_alarmBusiness.adapter  = adapterMainBusiness

            recycler_alarmFarm.layoutManager = LinearLayoutManager(myPageActivity)
            recycler_alarmCommunity.layoutManager = LinearLayoutManager(myPageActivity)
            recycler_alarmBusiness.layoutManager = LinearLayoutManager(myPageActivity)


            swipe_in_alarm.isRefreshing = false
            progressBar_alarm.visibility = View.GONE

        }
    }
}