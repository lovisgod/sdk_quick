package com.interswitchng.smartpos.modules.setup.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.FingerprintBottomDialog
import com.interswitchng.smartpos.modules.main.dialogs.MerchantCardDialog
import com.interswitchng.smartpos.shared.activities.BaseFragment

class MerchantCardDialogFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_merchant_card_dialog_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDialog()

    }

    private fun showDialog() {
        val fingerprintDialog = FingerprintBottomDialog(isAuthorization = true) { isValidated ->
            if (isValidated) {
                navigate(MerchantCardDialogFragmentDirections.iswActionGotoReportFragment())
            } else {
                toast("Fingerprint Verification Failed!!")
                navigateUp()
            }
        }
        val dialog = MerchantCardDialog { type ->
            when (type) {
                MerchantCardDialog.AUTHORIZED -> {

                    navigate(MerchantCardDialogFragmentDirections.iswActionGotoReportFragment())
                }
                MerchantCardDialog.FAILED -> {
                    toast("Unauthorized Access!!")
                    navigateUp()
                }
                MerchantCardDialog.USE_FINGERPRINT -> {
                    fingerprintDialog.show(childFragmentManager, FingerprintBottomDialog.TAG)
                }
            }
        }
        dialog.show(childFragmentManager, MerchantCardDialog.TAG)

    }

    companion object {
        const val TAG = " Merchant Card Dialog Fragment"
    }
}