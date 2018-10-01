package me.gr.githubbrowser.adapter

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter("app:isGone")
fun setViewVisibility(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}