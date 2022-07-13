package com.example.nuru.model.data.community

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import kotlin.collections.ArrayList

data class CommunityEntity(
    val image: ArrayList<Uri>,
    val contents: String,
    val title: String,
    val writer: String,
    val likeId: ArrayList<String>,
    val commentsNum: Long,
    val time: FieldValue
    )