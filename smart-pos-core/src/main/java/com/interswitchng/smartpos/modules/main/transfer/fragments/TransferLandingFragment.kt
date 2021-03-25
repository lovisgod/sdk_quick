package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.customdailog
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_transfer_landing.*
import kotlinx.android.synthetic.main.isw_fragment_transfer_landing.isw_transfer_card
import kotlinx.android.synthetic.main.isw_pos_landing_horizontal_scroll_view.*
import org.koin.android.viewmodel.ext.android.viewModel


class TransferLandingFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_landing

    private lateinit var message : Dialog

    private val cardViewModel : CardViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // check for settlement account setup for specific transfer
        checkForSettlement()
        handleClicks()
        terminalInfo?.let {
            cardViewModel.getToken(it)
        }

    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun handleClicks() {
        isw_transfer_card.setOnClickListener {
            var paymentModel = PaymentModel()
            paymentModel.type = PaymentModel.TransactionType.TRANSFER
            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswAmountfragment(
                    paymentModel = paymentModel,
                    BankModel = BankModel("","", ""),
                    BeneficiaryModel = BeneficiaryModel()
            )
//          val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswTransferinputfragment()
            findNavController().navigate(action)
            }

        isw_bill_pay_card.setOnClickListener {
            logger.log("clicked the listener")
            var paymentModel = PaymentModel()
            paymentModel.type = PaymentModel.TransactionType.BILL_PAYMENT
            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswDataplanfragment2()
            navigate(action)
        }

        isw_settings_icon.setOnClickListener {
           var popup = PopupMenu(this.requireContext(), isw_settings_icon, 0, 0, R.style.IswPopupMenuStyle)
            var inflater = popup.menuInflater
            inflater.inflate(R.menu.isw_generic_transfer_settings_options, popup.menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.configSettings -> {
                        iswPos.goToSettingsUpdatePage()
                        return@setOnMenuItemClickListener true
                    }

                    R.id.setSettlementAccount -> {
                        findNavController().navigate(R.id.isw_transfersettlementpinfragment)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.viewSettlementAccount -> {
                        findNavController().navigate(R.id.isw_viewsettlementaccountfragment)
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        iswPos.goToSettingsUpdatePage()
                        return@setOnMenuItemClickListener true
                    }
                }
            }
            popup.show()
        }
    }


//    check for settlement account setup for specific transfer
    private fun checkForSettlement() {
        if (Prefs.getString(Constants.SETTLEMENT_ACCOUNT_NUMBER, "").isNullOrEmpty()) {
             message = customdailog(context = this.requireContext(),
                    message = this.requireContext().resources.getString(R.string.isw_concat,
                            "Please setup your settlement account number") )
            {
                message.dismiss()
                findNavController().navigate(R.id.isw_transfersettlementpinfragment)
            }
        }
    }

    // always check for settlement account setup for specific transfer
//    override fun onResume() {
//        super.onResume()
//        checkForSettlement()
//    }


    companion object {
        @JvmStatic
        fun newInstance() = TransferLandingFragment()
        const val TAG = "TRANSFER LANDING FRAGMENT"
    }
}