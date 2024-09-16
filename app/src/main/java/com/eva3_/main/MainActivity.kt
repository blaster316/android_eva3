package com.eva3_.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eva3_.map.MapsActivity
import com.eva3_.photo.PhotoActivity
import com.eva3_.model.Place
import com.eva3_.adapters.PlaceAdapter
import com.eva3_.R
import com.eva3_.takePhoto.TakePhotoActivity
import com.eva3_.takephotowithlocation.TakePhotoWithLocationActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val placesViewModel: MainViewModel by viewModels()

    lateinit var placeAdapter : PlaceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvPlaces = findViewById<RecyclerView>(R.id.rv_places)
        rvPlaces.layoutManager = LinearLayoutManager(this)
        placeAdapter = PlaceAdapter(this, placesViewModel.places.value ?: mutableListOf()) { photoUri ->
            openPhotoActivity(photoUri)
        }
        rvPlaces.adapter = placeAdapter
        placesViewModel.places.observe(this, Observer { places ->
            placeAdapter.update(places)
        })

        findViewById<MaterialButton>(R.id.btn_map).setOnClickListener {
            checkLocationPermissionOpenMap()
        }

        findViewById<MaterialButton>(R.id.btn_photo).setOnClickListener {
            checkLocationPermissionOpenAddPlace()
        }
    }

    private fun checkLocationPermissionOpenAddPlace() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            val manager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {

                openAddPlace()
            }
        }
    }

    private fun checkLocationPermissionOpenMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            val manager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {

                openMap()
            }
        }
    }


    private val openMapLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val latitude = result.data?.getDoubleExtra("latitude", 0.0)
            val longitude = result.data?.getDoubleExtra("longitude", 0.0)
            openAdPlaceWithLocation(latitude, longitude)
        }
    }

    private fun openAdPlaceWithLocation(latitude: Double?, longitude: Double?) {
        val intent = Intent(this,TakePhotoWithLocationActivity::class.java)
        intent.putExtra("latitude",latitude)
        intent.putExtra("longitude",longitude)
        addPlaceWithLocation.launch(intent)
    }

    private fun openMap() {
        val intent = Intent(this, MapsActivity::class.java)
        openMapLauncher.launch(intent)
    }


    private val addPlaceWithLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val place = result.data!!.getSerializableExtra("place") as Place
            placesViewModel.addPlace(place)
            placeAdapter.notifyDataSetChanged()
        }
    }

    private val addPlaceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val place = result.data!!.getSerializableExtra("place") as Place
            placesViewModel.addPlace(place)
            placeAdapter.notifyDataSetChanged()
        }
    }

    private fun openAddPlace() {
        val intent = Intent(this, TakePhotoActivity::class.java)
        addPlaceLauncher.launch(intent)
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Tu GPS esta desactivado, deseas habilitarlo??")
            .setCancelable(false)
            .setPositiveButton("Si") { dialog, id ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
                onBackPressedDispatcher.onBackPressed()
            }
        val alert = builder.create()
        alert.show()
    }

    var locationPermissionRequest =
        registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String, Boolean>> { result: Map<String, Boolean> ->
                val fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                )
                val coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false
                )
                if (fineLocationGranted != null && fineLocationGranted) {

                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    onBackPressed()
                    Toast.makeText(
                        this,
                        "Se necesitan los permisos completos para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onBackPressedDispatcher.onBackPressed()
                    Toast.makeText(
                        this,
                        "Se necesitan los permisos para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )


    private fun openPhotoActivity(photoUri: Uri) {
        val intent = Intent(this, PhotoActivity::class.java).apply {
            putExtra("PHOTO_URI", photoUri.toString())
        }
        startActivity(intent)
    }
}