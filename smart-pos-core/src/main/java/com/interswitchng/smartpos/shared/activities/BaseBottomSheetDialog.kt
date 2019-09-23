package com.interswitchng.smartpos.shared.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.R

abstract class BaseBottomSheetDialog <BINDING: ViewDataBinding> : BottomSheetDialogFragment() {

    protected lateinit var binding: BINDING

    @LayoutRes abstract fun getLayoutId(): Int

    override fun getTheme(): Int = R.style.IswBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }
}