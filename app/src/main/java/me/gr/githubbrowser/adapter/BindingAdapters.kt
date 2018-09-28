package me.gr.githubbrowser.adapter

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

@BindingAdapter("isGone")
fun setViewVisibility(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(imageView).load(imageUrl).into(imageView)
    }
}