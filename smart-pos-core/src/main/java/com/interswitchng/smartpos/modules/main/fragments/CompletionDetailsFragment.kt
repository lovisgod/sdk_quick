package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_completion_details.*

class CompletionDetailsFragment : BaseFragment(TAG) {

    private val completionDetailsFragmentArgs by navArgs<CompletionDetailsFragmentArgs>()
    private val paymentModel by lazy { completionDetailsFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_completion_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_pay_balance.setOnClickListener {
            val direction = CompletionDetailsFragmentDirections.iswActionGotoFragmentAmount(paymentModel)
            navigate(direction)
        }
    }

    companion object {
        const val TAG = "Completion Details Fragment"
    }
}