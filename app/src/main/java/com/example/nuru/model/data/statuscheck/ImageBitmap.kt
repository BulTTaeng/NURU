package com.example.nuru.model.data.statuscheck

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageBitmap (
    val imageBitmap : Bitmap
        ): Parcelable