package ru.ifmo.nefedov.task11.imagelist.imageListView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.ifmo.nefedov.task11.imagelist.R

class ImageViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val imageView: ImageView = root.findViewById(R.id.item_image)
    val descriptionTextView: TextView = root.findViewById(R.id.item_description)
}