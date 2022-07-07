package com.example.nuru.Model.Data.Farm


import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize // 인텐트로 넘기기 위해서
data class Farm(
    var farm_id : String,
    var farm_name: String,
    var farm_img : ArrayList<String>,
    var farm_latLng: LatLng,
    var farm_address: String,
    var farm_owner: String,
    var position : Int,
    var products : String
): Parcelable