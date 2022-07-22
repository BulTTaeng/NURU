package com.example.nuru.viewmodel.alarm

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.mainbusiness.MainBusinessEntity
import com.example.nuru.repository.alarm.AlarmRepository
import com.example.nuru.repository.fitcheck.FitCheckRepository
import com.example.nuru.repository.mainbusiness.MainBusinessRepository

class AlarmViewModel() : ViewModel() {

    private val repo = AlarmRepository()
    private val communityData = repo.communityData
    private val farmData = repo.farmData
    private val mainBusinessData = repo.mainBusinessData

    fun fetchCommunityData(): LiveData<MutableList<String>> {
        return communityData
    }

    fun fetchFarmData(): LiveData<MutableList<String>> {
        return farmData
    }

    fun fetchMainBusinessData(): LiveData<MutableList<String>> {
        return mainBusinessData
    }

    suspend fun getCommunityAlarm() : Boolean{
        return repo.getCommunityAlarm()
    }

    suspend fun getFarmAlarm() : Boolean{
        return repo.getFarmAlarm()
    }

    suspend fun getMainBusinessAlarm() : Boolean{
        return repo.getMainBusinessAlarm()
    }


}