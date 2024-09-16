package com.eva3_.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eva3_.model.Place

class MainViewModel: ViewModel() {
    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> get() = _places

    fun addPlace(place: Place) {
        _places.value = _places.value?.toMutableList()?.apply { add(place) } ?: listOf(place)
    }

    fun updatePlaces(newPlaces: List<Place>) {
        _places.value = newPlaces
    }
}