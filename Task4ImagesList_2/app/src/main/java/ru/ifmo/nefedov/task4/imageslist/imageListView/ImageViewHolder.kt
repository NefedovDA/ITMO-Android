package ru.ifmo.nefedov.task4.imageslist.imageListView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.nefedov.task4.imageslist.R

class ImageViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val imageView: ImageView = root.findViewById(R.id.item_image)
    val descriptionTextView: TextView = root.findViewById(R.id.item_description)
}