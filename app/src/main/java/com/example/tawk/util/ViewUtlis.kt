package com.example.tawk.util

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.tawk.R

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun ImageView.invert() {
    val matrix = floatArrayOf(
        -1f, 0f, 0f, 0f, 255f,
        0f, -1f, 0f, 0f, 255f,
        0f, 0f, -1f, 0f, 255f,
        0f, 0f, 0f, 1f, 0f
    )
    val colorMatrix = ColorMatrix(matrix)
    val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
    this.setColorFilter(colorMatrixColorFilter)
}

fun ImageView.load(url: String?, isPlaceholder: Boolean, isCircleCrop: Boolean) {
    var builder = Glide.with(this).load(url)
    if (isPlaceholder) builder.placeholder(R.drawable.placeholder_square)
    if (isCircleCrop) builder.circleCrop()
    builder.into(this)
}

fun ImageView.show() {
    visibility = View.VISIBLE
}

fun ImageView.hide() {
    visibility = View.GONE
}

