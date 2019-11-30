package ru.ifmo.nefedov.task4imageslist.imagePreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.nefedov.task4imageslist.R

class ImagePreviewAdapter(
    private val imagePreviewList: List<ImagePreview>,
    private val onClick: (ImagePreview) -> Unit
) : RecyclerView.Adapter<ImagePreviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePreviewViewHolder {
        val holder = ImagePreviewViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.image_preview_item,
                parent,
                false
            )
        )
        holder.root.setOnClickListener {
            onClick(imagePreviewList[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount(): Int = imagePreviewList.size

    override fun onBindViewHolder(holder: ImagePreviewViewHolder, position: Int) =
        with(imagePreviewList[position]) {
            holder.header.text = header
            holder.image.setImageResource(imageId)
            holder.description.text = description
        }
}