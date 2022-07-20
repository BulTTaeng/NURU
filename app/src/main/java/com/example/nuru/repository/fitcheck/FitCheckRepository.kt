package com.example.nuru.repository.fitcheck

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FitCheckRepository {

    val db = FirebaseFirestore.getInstance()
    var list  = ArrayList<String>()
    var cityList = ArrayList<String>()
    var productList = ArrayList<String>()

    val _mutableCityData = MutableLiveData<MutableList<String>>()
    val cityData: LiveData<MutableList<String>>
        get() = _mutableCityData

    val _mutableCountryData = MutableLiveData<MutableList<String>>()
    val countryData: LiveData<MutableList<String>>
        get() = _mutableCountryData

    val _mutableProductData = MutableLiveData<MutableList<String>>()
    val productData: LiveData<MutableList<String>>
        get() = _mutableProductData



    init {
    }

    suspend fun getCountry(city : String) : Boolean{
        return try {
            db.collection("city").document(city).get().addOnSuccessListener {
                list = it["country"] as ArrayList<String>
                _mutableCountryData.value = list
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }

    suspend fun getCity() : Boolean{
        return try {
            db.collection("city").get().addOnSuccessListener { snapshot ->
                cityList.clear()
                for(it in snapshot){
                    cityList.add(it.id)
                }

                _mutableCityData.value = cityList
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }

    suspend fun getProduct() : Boolean{
        return try {
            db.collection("product").document("aaa").get().addOnSuccessListener { it ->
                productList = it["productName"] as ArrayList<String>
                _mutableProductData.value = productList
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("error" , e.toString())
            false
        }
    }




}