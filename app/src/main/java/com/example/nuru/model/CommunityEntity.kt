package com.example.nuru.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize // 인텐트로 넘기기 위해서
data class CommunityEntity(
    val image : ArrayList<String>,
    val contents : String,
    val title : String,
    val writer : String,
    val id : String,
    val like : ArrayList<String>,
    val comments : Long,
    val time : Date
) : Parcelable