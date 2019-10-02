package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.AdminAccessDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_pin.*

class PinFragment : BaseFragment(TAG) {

    private val pinFragmentArgs by navArgs<PinFragmentArgs>()
    private val payment by lazy { pinFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_pin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_enter.setOnClickListener {
            when (payment.type) {
                PaymentModel.MakePayment.COMPLETION -> {
                    val dialog = AdminAccessDialog {
                        val direction = PinFragmentDirections.iswActionGotoFragmentCompletionDetails(payment)
                        navigate(direction)
                    }
                    dialog.show(childFragmentManager, AdminAccessDialog.TAG)
                }
                else -> {
                    val direction = PinFragmentDirections.iswActionGotoFragmentProcessingTransaction(payment)
                    navigate(direction)
                }
            }
        }
    }

    companion object {
        const val TAG = "Pin Fragment"
    }
}