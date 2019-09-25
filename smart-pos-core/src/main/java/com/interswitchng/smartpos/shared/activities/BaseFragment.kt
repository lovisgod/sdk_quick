package com.interswitchng.smartpos.shared.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.shared.utilities.Logger

abstract class BaseFragment (fragmentName: String) : Fragment() {

    abstract val layoutId: Int

    protected lateinit var rootView: View
    protected val logger by lazy { Logger.with(fragmentName) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(layoutId, container, false)
        return rootView
    }

    protected fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    protected fun navigateUp() = findNavController().navigateUp()

    protected fun navigate(directionId: Int) {
        findNavController().navigate(directionId)
    }
}