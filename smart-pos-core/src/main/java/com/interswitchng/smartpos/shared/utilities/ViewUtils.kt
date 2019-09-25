package com.interswitchng.smartpos.shared.utilities

import android.content.Context
import android.view.View
import android.widget.Toast

fun Context.toast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

fun View.hideMe() {
    if (visibility == View.VISIBLE && alpha == 1f) {
        animate()
            .alpha(0f)
            .withEndAction { visibility = View.GONE }
    }
}

fun View.showMe() {
    if (visibility == View.GONE && alpha == 0f) {
        animate()
            .alpha(1f)
            .withEndAction { visibility = View.VISIBLE }
    }
}