package com.interswitchng.interswitchpossdk.utils

import android.content.Context

object Utilities {

    fun getJson(context: Context, path: String): String {
        val file = context.resources.assets.open(path)
        return String(file.readBytes())
    }
}
