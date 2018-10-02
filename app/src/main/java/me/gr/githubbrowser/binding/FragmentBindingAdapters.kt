package me.gr.githubbrowser.binding

import android.databinding.BindingAdapter
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.Glide
import javax.inject.Inject

class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {

    @BindingAdapter("app:imageUrl")
    fun setImageUrl(imageView: ImageView, imageUrl: String?) {
        Glide.with(fragment).load(imageUrl).into(imageView)
    }
}