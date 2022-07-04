package com.example.nuru.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize // 인텐트로 넘기기 위해서
data class Comments(
    var id : String,
    var communityId : String,
    var commentsContents: String,
    var writer: String,
    val time: Date,
    val name: String
): Parcelable , Serializable