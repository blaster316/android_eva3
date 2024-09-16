package com.eva3_.photo

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.eva3_.R

class PhotoActivity : AppCompatActivity() {

    lateinit var imgphoto : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        imgphoto = findViewById(R.id.img_photo)
        val photoUri = intent.getStringExtra("PHOTO_URI")
        if (photoUri != null) {
            imgphoto.setImageURI(Uri.parse(photoUri))
        }
    }
}