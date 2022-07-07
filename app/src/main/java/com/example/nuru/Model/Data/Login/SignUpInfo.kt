package com.example.nuru.Model.Data.Login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpInfo (
    val userId: String,
    val name: String,
    val email: String ,
    val type : String
    ): Parcelable