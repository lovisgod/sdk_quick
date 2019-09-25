package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.databinding.IswFragmentTransactionBinding
import com.interswitchng.smartpos.modules.main.dialogs.MakePaymentDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.payment
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.Logger

class TransactionFragment: BaseFragment<IswFragmentTransactionBinding>(TAG) {

    override fun getLayoutId(): Int = R.layout.isw_fragment_transaction

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.iswMakePaymentCard.setOnClickListener {
            val sheet = MakePaymentDialog {
                //Handle click listener here
                logger.logErr("Item number clicked ===== $it")
                when (it) {
                    0 -> {
                        val payment = payment {
                            type = 0
                        }
                        navigate(TransactionFragmentDirections.iswActionGotoFragmentTransaction(payment))
                    }
                }
            }
            sheet.show(childFragmentManager, TAG)
        }
    }

    companion object {
        const val TAG = "TRANSACTION FRAGMENT"
    }
}
