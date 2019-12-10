package com.interswitchng.smartpos.modules.sendmoney


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import kotlinx.android.synthetic.main.isw_fragment_transfer_pin.*


class PinFragment : BaseFragment(TAG) {

    private val pinFragmentArgs by navArgs<PinFragmentArgs>()
    private val payment by lazy { pinFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_pin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {

        backImg.setOnClickListener {
            navigateUp()
        }

        if (payment.type == PaymentModel.TransactionType.ECHANGE) {
            isw_merchant_name.text = "Merchant Code"
        }

        isw_amount.text = payment.formattedAmount

        val transaction = TransactionResponseModel()
        isw_proceed.setOnClickListener {
            val direction = PinFragmentDirections.actionIswPinfragmentToTransactionSentFragment(payment)
            navigate(direction)
        }


    }


    companion object {
        const val TAG = "ECASH FRAGMENT"
    }

}
