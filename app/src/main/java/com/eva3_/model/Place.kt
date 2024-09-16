package com.eva3_.model

import java.io.Serializable

data class Place (
    var namePlace : String,
    var latitude : Double,
    var longitude : Double,
    var listPhotos : MutableList<String> = mutableListOf()
) : Serializable