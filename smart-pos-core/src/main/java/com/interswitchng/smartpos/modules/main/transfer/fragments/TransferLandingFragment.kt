package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_transfer_landing.*


class TransferLandingFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_landing


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()
    }

    private fun handleClicks() {
        isw_transfer_card.setOnClickListener {
            var paymentModel = PaymentModel()
            paymentModel.type = PaymentModel.TransactionType.TRANSFER
            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswAmountfragment(paymentModel)
            findNavController().navigate(action)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = TransferLandingFragment()
        const val TAG = "TRANSFER LANDING FRAGMENT"
    }
}