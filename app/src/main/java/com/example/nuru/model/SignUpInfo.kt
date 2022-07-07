package com.example.nuru.model

import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpInfo (
    val userId: String,
    val name: String,
    val email: String ,
    val type : String
    ): Parcelable