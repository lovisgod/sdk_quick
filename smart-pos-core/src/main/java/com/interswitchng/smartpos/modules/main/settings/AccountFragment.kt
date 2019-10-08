package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_settings_account.*

class AccountFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_settings_account

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.isw_settings_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
    }

    private fun handleClicks() {
        isw_fingerprint_container.setOnClickListener {
            it.findNavController().navigate(R.id.isw_goto_account_fingerprint_fragment_action)
        }

        isw_settings_toolbar_label.setOnClickListener {
            navigateUp()
        }
    }

    companion object {
        const val TAG = "Account Fragment"
    }
}
