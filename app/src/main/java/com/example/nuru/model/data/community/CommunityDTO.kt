package com.example.nuru.model.data.community

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize // 인텐트로 넘기기 위해서
data class CommunityDTO(
    var image : ArrayList<String>,
    var contents : String,
    var title : String,
    val writer : String,
    val id : String,
    var like : ArrayList<String>,
    var comments : Long,
    val time : Date
) : Parcelable