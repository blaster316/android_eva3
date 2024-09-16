package com.eva3_.takePhoto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eva3_.adapters.PhotoAdapterSave
import com.eva3_.model.Place
import com.eva3_.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton

class TakePhotoActivity : AppCompatActivity() {
    private lateinit var mylocationUpdateCallback: LocationCallback
    var client: FusedLocationProviderClient? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location: Location? = null

    var mLocationRequest: LocationRequest? = null
    protected var locationManager: LocationManager? = null


    lateinit var rv_photos : RecyclerView
    lateinit var btn_take_photo : MaterialButton
    lateinit var btn_save : MaterialButton
    lateinit var edt_name_place : EditText
    private val viewModel: TakePhotoViewModel by viewModels()

    lateinit var photoAdapter: PhotoAdapterSave
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
        rv_photos = findViewById(R.id.rv_photos_save)
        btn_take_photo = findViewById(R.id.btn_take_photo)
        btn_save = findViewById(R.id.btn_save)
        edt_name_place = findViewById(R.id.edt_name_place)

        rv_photos.layoutManager = GridLayoutManager(this, 6)
        photoAdapter = PhotoAdapterSave(viewModel.photoList)
        rv_photos.adapter = photoAdapter


        startLocationUpdates()


        btn_take_photo.setOnClickListener {
            openCamera()
        }


        btn_save.setOnClickListener {
            val placeName = edt_name_place.text.toString().trim()

            if (placeName.isEmpty()) {
                Toast.makeText(this, "El nombre del lugar no puede estar vacío", Toast.LENGTH_SHORT).show()
            } else {
                val place = Place(
                    namePlace = placeName,
                    latitude = viewModel.latitude,
                    longitude = viewModel.longitude,
                    listPhotos = viewModel.photoList.map { it.toString() }.toMutableList()
                )

                val resultIntent = Intent()
                resultIntent.putExtra("place", place)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun openCamera() {
        ImagePicker.with(this@TakePhotoActivity)
            .compress(1024)
            .cameraOnly()
            .maxResultSize(
                800,
                800
            )
            .createIntent { intent ->
                cameraLaunch.launch(intent)
            }
    }

    private val cameraLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if(result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                viewModel.photoList.add(it.toString())
                photoAdapter.update(viewModel.photoList)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.interval = 12000
        mLocationRequest!!.fastestInterval = 12000
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)


        val lastLocation = getLocation()
        if(lastLocation != null) {
            onLocationChanged(lastLocation)
        }
        mylocationUpdateCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d("location App : ", "ready")
                if(locationResult.lastLocation != null) {
                    onLocationChanged(locationResult.lastLocation!!)
                }

            }
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest!!, mylocationUpdateCallback, Looper.getMainLooper()
        )
    }

    @JvmName("getLocation1")
    fun getLocation(): Location ?{
        try {
            locationManager =
                getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                this.canGetLocation = false
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000, 0f, locationProviderListener
                        )
                        android.util.Log.d("locacion", "from network")
                        return location!!
                    }
                } else if (isGPSEnabled) {
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000, 0f, locationProviderListener
                        )
                        android.util.Log.d("locacion", "from gps")
                        return location!!
                    }
                }
            }
        } catch (e: java.lang.SecurityException) {
        } catch (e: java.lang.Exception) {
        }
        return location
    }

    var locationProviderListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            try {
                viewModel.latitude = location.latitude
                viewModel.longitude = location.longitude
            } catch (e: java.lang.Exception) {
            }
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    private fun onLocationChanged(locationResult: Location) {

        try {
            val latLng = LatLng(locationResult.latitude, locationResult.longitude)

            Log.e("this"  ,"latitude " + locationResult.latitude + " longitude "+locationResult.longitude)
            viewModel.latitude = latLng.latitude
            viewModel.longitude = latLng.longitude
        } catch (e: Exception) {
            Log.d("location listener", "null " + e.cause)
        }
    }

    override fun onDestroy() {
        try {
            locationManager!!.removeUpdates(locationProviderListener)
            client!!.removeLocationUpdates(mylocationUpdateCallback)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }


    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}