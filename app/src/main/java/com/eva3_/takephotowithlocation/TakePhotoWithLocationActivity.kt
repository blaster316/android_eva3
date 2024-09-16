package com.eva3_.takephotowithlocation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eva3_.R
import com.eva3_.adapters.PhotoAdapterSave
import com.eva3_.takePhoto.TakePhotoViewModel
import com.eva3_.model.Place
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton

class TakePhotoWithLocationActivity : AppCompatActivity() {
    lateinit var rv_photos : RecyclerView
    lateinit var btn_take_photo : MaterialButton
    lateinit var btn_save : MaterialButton
    lateinit var edt_name_place : EditText
    private val viewModel: TakePhotoViewModel by viewModels()

    lateinit var photoAdapter: PhotoAdapterSave
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo_with_location)
        rv_photos = findViewById(R.id.rv_photos_save)
        btn_take_photo = findViewById(R.id.btn_take_photo)
        btn_save = findViewById(R.id.btn_save)
        edt_name_place = findViewById(R.id.edt_name_place)

        rv_photos.layoutManager = GridLayoutManager(this, 6)
        photoAdapter = PhotoAdapterSave(viewModel.photoList)
        rv_photos.adapter = photoAdapter

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        viewModel.latitude = latitude
        viewModel.longitude = longitude

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
        ImagePicker.with(this)
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

    private val cameraLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                viewModel.photoList.add(it.toString())
                photoAdapter.update(viewModel.photoList)
            }
        }
    }
}