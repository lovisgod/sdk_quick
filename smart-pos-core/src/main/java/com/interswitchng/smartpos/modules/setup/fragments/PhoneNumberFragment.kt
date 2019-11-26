package com.interswitchng.smartpos.modules.setup.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.FingerprintBottomDialog
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import kotlinx.android.synthetic.main.isw_fragment_phone_number.*
import org.koin.android.ext.android.inject

class PhoneNumberFragment : BaseFragment(TAG) {

    private val store by inject<KeyValueStore>()

    override val layoutId: Int
        get() = R.layout.isw_fragment_phone_number

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isw_add_fingerprint.setOnClickListener {
            val phoneNumber = isw_phone_number.text.toString()
            store.saveString("M3RCHANT_PHONE", phoneNumber)
            val dialog = FingerprintBottomDialog {
                if (it) {
                    toast("Fingerprint Captured Successfully")
                    requireActivity().finish()
                }
                else toast("Failed to capture fingerprint")
            }
            dialog.show(childFragmentManager, FingerprintBottomDialog.TAG)
        }
    }

    companion object {
        const val TAG = "Phone Number Fragment"
    }
}