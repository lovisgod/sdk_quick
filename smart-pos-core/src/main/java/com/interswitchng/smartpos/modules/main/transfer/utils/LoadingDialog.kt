package com.interswitchng.smartpos.modules.main.transfer.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.interswitchng.smartpos.R

class LoadingDialog(): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       var rootView: View = inflater.inflate(R.layout.isw_transfer_custom_loading_dialog, container, false)
       return rootView
    }
}