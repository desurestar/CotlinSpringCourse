package com.example.front.util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

// View extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Fragment extensions
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}
