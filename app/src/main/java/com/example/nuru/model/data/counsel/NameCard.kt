package com.example.nuru.model.data.counsel

import com.google.firebase.firestore.QueryDocumentSnapshot

data class NameCard (
    val company : String,
    val address : String,
    val tel : String,
) {
    object NameCardMapper {
        fun fromDB(json: QueryDocumentSnapshot) : NameCard {
            return NameCard(
                json["company"].toString(),
                json["address"].toString(),
                json["tel"].toString()
            )
        }
    }
}