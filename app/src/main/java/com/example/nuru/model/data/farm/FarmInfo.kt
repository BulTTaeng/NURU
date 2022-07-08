package com.example.nuru.model.data.farm

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize // 인텐트로 넘기기 위해서
data class FarmInfo(
    var weather :String,
    var humidity :Double,
    var information :String,
    var temperature :Double
): Parcelable