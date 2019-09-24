package com.interswitchng.smartpos.shared.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.R

abstract class BaseBottomSheetDialog : BottomSheetDialogFragment() {

    protected lateinit var rootView: View

    @LayoutRes abstract fun getLayoutId(): Int

    override fun getTheme(): Int = R.style.IswBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(getLayoutId(), container, false)
        return rootView
    }
}