package ru.ifmo.nefedov.task4imageslist.views.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_preview.view.*
import ru.ifmo.nefedov.task4imageslist.R
import ru.ifmo.nefedov.task4imageslist.model.Image

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    private var images: List<Image> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val holder = ImagesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_preview,
                parent,
                false
            )
        )

        holder.imageView.setOnClickListener {}

        return holder
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        //holder.imageView.setImageBitmap(images[position].bitmap)
    }

    fun setImages(images: List<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    class ImagesViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val imageView: ImageView = root.item_photo
    }
}