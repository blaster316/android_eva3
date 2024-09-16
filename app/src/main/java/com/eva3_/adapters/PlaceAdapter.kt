package com.eva3_.adapters
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eva3_.model.Place
import com.eva3_.R

class PlaceAdapter(
    private val context: Context,
    private var places: List<Place>,
    private val onPhotoClick: (Uri) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    override fun getItemCount() = places.size

    fun update(places: List<Place>) {
        this.places = places
        notifyDataSetChanged()
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namePlace: TextView = itemView.findViewById(R.id.text_name_place)
        private val latitude: TextView = itemView.findViewById(R.id.text_latitude)
        private val longitude: TextView = itemView.findViewById(R.id.text_longitude)
        private val rvPhotos: RecyclerView = itemView.findViewById(R.id.rv_photos)

        fun bind(place: Place) {
            namePlace.text = place.namePlace
            latitude.text = place.latitude.toString()
            longitude.text = place.longitude.toString()

            rvPhotos.layoutManager = GridLayoutManager(context, 6)
            rvPhotos.adapter = PhotoAdapter(context, place.listPhotos, onPhotoClick)
        }
    }
}
