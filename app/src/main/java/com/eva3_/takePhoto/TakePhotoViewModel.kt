package com.eva3_.takePhoto

import androidx.lifecycle.ViewModel

class TakePhotoViewModel : ViewModel() {
    val photoList = mutableListOf<String>()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}