package com.example.morkborgcharactersheet.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object DataBindingAdapter {
    @BindingAdapter("app:src")
    @JvmStatic
    fun setImageViewDrawable(imageView: ImageView, res: Drawable) {
        imageView.setImageDrawable(res)
    }

    @BindingAdapter("app:src")
    @JvmStatic
    fun setImageViewResource(imageView: ImageView, res: Int) {
        imageView.setImageResource(res)
    }
}