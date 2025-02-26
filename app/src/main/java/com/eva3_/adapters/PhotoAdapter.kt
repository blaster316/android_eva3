package com.eva3_.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.eva3_.R

class PhotoAdapter(
    private val context: Context,
    private val photos: List<String>,
    private val onPhotoClick: (Uri) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photos[position]
        holder.bind(Uri.parse(photoUri))
    }

    override fun getItemCount() = photos.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(photoUri: Uri) {
            imageView.setImageURI(photoUri)
            imageView.setOnClickListener { onPhotoClick(photoUri) }
        }
    }
}
