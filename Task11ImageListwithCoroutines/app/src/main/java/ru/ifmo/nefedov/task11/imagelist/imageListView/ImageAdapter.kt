package ru.ifmo.nefedov.task11.imagelist.imageListView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.nefedov.task11.imagelist.R
import ru.ifmo.nefedov.task11.imagelist.data.SmallImage

class ImageAdapter(
    val onClick: (SmallImage) -> Unit
) : RecyclerView.Adapter<ImageViewHolder>() {

    private var images: List<SmallImage> = listOf()

    fun getImages(): List<SmallImage> {
        return images
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val holder = ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image_preview,
                parent,
                false
            )
        )

        holder.root.setOnClickListener {
            onClick(images[holder.adapterPosition])
        }

        return holder
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]

        holder.imageView.setImageBitmap(image.bitmap)
        image.description?.let { holder.descriptionTextView.text = it }
    }

    fun setImages(images: List<SmallImage>) {
        this.images = images
        notifyDataSetChanged()
    }
}