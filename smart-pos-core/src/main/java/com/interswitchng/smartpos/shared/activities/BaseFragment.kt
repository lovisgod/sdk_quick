package com.interswitchng.smartpos.shared.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.shared.utilities.Logger

abstract class BaseFragment <BINDING: ViewDataBinding>(fragmentName: String) : Fragment() {

    @LayoutRes abstract fun getLayoutId(): Int

    protected lateinit var binding: BINDING
    protected val logger by lazy { Logger.with(fragmentName) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    protected fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    protected fun navigate(directionId: Int) {
        findNavController().navigate(directionId)
    }
}