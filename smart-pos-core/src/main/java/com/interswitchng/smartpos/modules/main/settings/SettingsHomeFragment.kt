package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.FingerprintBottomDialog
import com.interswitchng.smartpos.modules.main.dialogs.MerchantCardDialog
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_settings_home.*

class SettingsHomeFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_settings_home

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.isw_settings_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
    }

    private fun handleClicks() {
        isw_account_container.setOnClickListener {
            authorizeAndPerformAction {
                it.findNavController().navigate(R.id.isw_goto_account_fragment_action)
            }
        }

        isw_settings_toolbar_label.setOnClickListener {
            navigateUp()
        }

        isw_download_settings_btn.setOnClickListener {
            authorizeAndPerformAction { iswPos.gotoSettlementSelection() }
        }
    }

    private fun authorizeAndPerformAction(action: () -> Unit) {
        val fingerprintDialog = FingerprintBottomDialog(isAuthorization = true) { isValidated ->
            if (isValidated) {
                action.invoke()
            } else {
                toast("Unauthorized Access!!")
            }
        }
        val dialog = MerchantCardDialog(isAuthorization = true) {
            when (it) {
                MerchantCardDialog.AUTHORIZED -> action.invoke()
                MerchantCardDialog.FAILED -> toast("Unauthorized Access!!")
                MerchantCardDialog.USE_FINGERPRINT -> fingerprintDialog.show(
                    requireFragmentManager(),
                    FingerprintBottomDialog.TAG
                )
            }
        }
        dialog.show(requireFragmentManager(), MerchantCardDialog.TAG)
    }

    companion object {
        const val TAG = "Setting Home Fragment"
    }
}
