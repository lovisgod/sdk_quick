package com.interswitchng.interswitchpossdk.shared.utilities

import android.content.Context
import android.support.v7.app.AlertDialog
import com.interswitchng.interswitchpossdk.R

object DialogUtils {

    fun getLoadingDialog(context: Context): AlertDialog {
        return AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.title_processing_payment)
                .setMessage(R.string.title_loading)
                .create()
    }
}