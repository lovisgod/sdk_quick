package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R
import kotlinx.android.synthetic.main.isw_activity_detail.*

class ActivityDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.isw_activity_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handlePrintReceiptClick()
    }

    private fun handlePrintReceiptClick() {
        isw_print_receipt_label.setOnClickListener {
            it.findNavController().navigate(R.id.isw_action_goto_fragment_receipt)
        }
    }
}
