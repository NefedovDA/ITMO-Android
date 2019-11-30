package ru.ifmo.nefedov.task4imageslist.imagePreview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image_preview_item.view.*

class ImagePreviewViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    val header: TextView = root.image_preview_header
    val image: ImageView = root.image_preview_image
    val description: TextView = root.image_preview_description
}