package com.example.nuru.model.data.farm

import com.google.protobuf.DoubleValue

data class FarmDAO(
    val farmAddress : String,
    val farmId : String,
    val farmOwner : String,
    val farmName : String,
    val latitude : Double,
    val longitude : Double,
    val products : String,
    val farmPhoto : ArrayList<String>,
    val farmAdmin : ArrayList<String>
)