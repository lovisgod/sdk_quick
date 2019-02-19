package com.interswitchng.interswitchpossdk.shared.utilities

import android.content.Context
import android.support.v7.app.AlertDialog
import com.interswitchng.interswitchpossdk.R

internal object DialogUtils {

    fun getLoadingDialog(context: Context): AlertDialog {
        return AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.isw_title_generating_code)
                .setMessage(R.string.isw_title_loading)
                .create()
    }

    fun getAlertDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
                .setTitle("An Error Occurred")
                .setMessage("Unable to generate code")
    }
}