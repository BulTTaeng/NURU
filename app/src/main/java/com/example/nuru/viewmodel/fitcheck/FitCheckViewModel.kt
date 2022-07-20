package com.example.nuru.viewmodel.fitcheck

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuru.model.data.mainbusiness.MainBusinessEntity
import com.example.nuru.repository.fitcheck.FitCheckRepository
import com.example.nuru.repository.mainbusiness.MainBusinessRepository

class FitCheckViewModel() : ViewModel() {

    private val repo = FitCheckRepository()
    private val cityData = repo.cityData
    private val countryData = repo.countryData
    private val productData = repo.productData

    var selectedCity : ObservableField<String> = ObservableField("주소 입력하기")
    var selectedCountry : ObservableField<String> = ObservableField("")
    var selectedProduct : ObservableField<String> = ObservableField("임산물 입력하기")

    fun fetchCityData(): LiveData<MutableList<String>> {
        return cityData
    }

    fun fetchCountryData(): LiveData<MutableList<String>> {
        return countryData
    }

    fun fetchProductData() : LiveData<MutableList<String>>{
        return productData
    }

    suspend fun getCountry(city : String) : Boolean{
        return repo.getCountry(city)
    }

    suspend fun getCity() : Boolean{
        return repo.getCity()
    }

    suspend fun getProduct() : Boolean{
        return repo.getProduct()
    }


}