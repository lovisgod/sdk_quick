package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_settings_account_fingerprint.*
import kotlinx.android.synthetic.main.isw_settings_home.*
import kotlinx.android.synthetic.main.isw_settings_home.isw_account_label

class FingerprintFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_settings_account_fingerprint

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.isw_settings_account_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
    }

    private fun handleClicks() {
        isw_account_label.setOnClickListener {
            navigateUp()
        }
        isw_registered_fingerprint_container.setOnClickListener {
            val direction = FingerprintFragmentDirections.iswGotoSettingsRegisteredFingerprint()
            navigate(direction)
        }
    }

    companion object {
        const val TAG = "Fingerprint Fragment"
    }
}
