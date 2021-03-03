package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_view_settle_ment_account.*

class ViewSettleMentAccountFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.isw_fragment_view_settle_ment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        account_no.text = Prefs.getString(Constants.SETTLEMENT_ACCOUNT_NUMBER, "")
        edit_image.setOnClickListener {
            findNavController().navigate(R.id.isw_transfersettlementpinfragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewSettleMentAccountFragment()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().popBackStack()
    }
}