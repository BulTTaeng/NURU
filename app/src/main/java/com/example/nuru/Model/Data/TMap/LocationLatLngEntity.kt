package com.example.nuru.Model.Data.TMap

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize // 인텐트로 넘기기 위해서
data class LocationLatLngEntity(
    val latitude: Float,
    val longitude: Float
) : Parcelable