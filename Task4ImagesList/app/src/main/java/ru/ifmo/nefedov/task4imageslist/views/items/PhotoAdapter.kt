package ru.ifmo.nefedov.task4imageslist.views.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_preview.view.*
import ru.ifmo.nefedov.task4imageslist.R

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private val photos: List<Any> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val holder = PhotoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_preview,
                parent,
                false
            )
        )

        holder.imageView.setOnClickListener {}

        return holder
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

    }

    class PhotoViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val imageView: ImageView = root.item_photo
    }
}