package com.eva3_.adapters
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.eva3_.R

class PhotoAdapterSave(
    private var photoList: List<String>
) : RecyclerView.Adapter<PhotoAdapterSave.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uri = photoList[position]
        holder.bind(Uri.parse(uri))
    }

    override fun getItemCount(): Int = photoList.size

    fun update(list : List<String>) {
        this.photoList = list
        notifyDataSetChanged()
    }
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(uri: Uri) {
            imageView.setImageURI(uri)
        }
    }
}
