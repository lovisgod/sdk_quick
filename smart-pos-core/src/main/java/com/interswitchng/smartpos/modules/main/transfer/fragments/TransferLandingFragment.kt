package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.clear
import com.interswitchng.smartpos.modules.main.transfer.customdailog
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.showErrorAlert
import com.interswitchng.smartpos.modules.main.transfer.showSuccessAlert
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_transfer_landing.*
import kotlinx.android.synthetic.main.isw_layout_insert_pin_reuseable.*
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
        Prefs.putBoolean("isAirtime", false)
        // check for settlement account setup for specific transfer
        checkForSettlement()
        handleClicks()
        setVersionCode()
        terminalInfo?.let {
            cardViewModel.getToken(it)
        }

    }

    private fun setVersionCode() {
        val xxx = try {
            this.requireContext().getPackageManager()
                    .getPackageInfo(this.requireContext().getPackageName(), 0)
                    .versionName
                    .split("-")
                    .get(0)
        } catch (e:Exception) {
            println(e)
        }
        val versionName = "Version: $xxx"
        version_name.text = versionName
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun handleClicks() {
        isw_transfer_hor_card.setOnClickListener {
            var paymentModel = PaymentModel()
            paymentModel.type = PaymentModel.TransactionType.TRANSFER
            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswAmountfragment(
                    paymentModel = paymentModel, BankModel = BankModel("","", ""), BeneficiaryModel = BeneficiaryModel())
//            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswTransferinputfragment()
            findNavController().navigate(action)
        }

        isw_bill_pay_card.setOnClickListener {
            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswChoosecategoryfragment()
            findNavController().navigate(action)
        }

//        isw_transfer_card.setOnClickListener {
//            var paymentModel = PaymentModel()
//            paymentModel.type = PaymentModel.TransactionType.TRANSFER
//            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswAmountfragment(
//                    paymentModel = paymentModel, BankModel = BankModel("","", ""), BeneficiaryModel = BeneficiaryModel())
////            val action = TransferLandingFragmentDirections.iswActionIswTransferlandingfragmentToIswTransferinputfragment()
//            findNavController().navigate(action)
//        }

        isw_settings_icon.setOnClickListener {
           var popup = PopupMenu(this.requireContext(), isw_settings_icon, 0, 0, R.style.IswPopupMenuStyle)
            var inflater = popup.menuInflater
            inflater.inflate(R.menu.isw_generic_transfer_settings_options, popup.menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.setTerminal -> {
//                        iswPos.goToSettingsUpdatePage()
                        val bundlex = Bundle()
                        bundlex.putString("configure_pass", "configure_pass")
                        findNavController().navigate(R.id.isw_transfersettlementpinfragment, bundlex)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.setSettlementAccount -> {
                        findNavController().navigate(R.id.isw_transfersettlementpinfragment)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.resetSettlementPin -> {
                        val bundle = Bundle()
                        bundle.putBoolean("reset_pin", true)
                        findNavController().navigate(R.id.isw_transfersettlementpinfragment, bundle)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.viewSettlementAccount -> {
                        findNavController().navigate(R.id.isw_viewsettlementaccountfragment)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.printEod -> {
                        findNavController().navigate(R.id.isw_reportfragment)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.transactionHistory -> {
                        findNavController().navigate(R.id.isw_transactionhistoryfragment)
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
//                        iswPos.goToSettingsUpdatePage()
                        return@setOnMenuItemClickListener true

                    }
                }
            }

            if (Prefs.getString(Constants.SETTLEMENT_PIN_SET, "").isNullOrEmpty()) {
                popup.menu.findItem(R.id.resetSettlementPin).isVisible = false
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.gravity = Gravity.END

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
//            Prefs.putString(Constants.SETTLEMENT_ACCOUNT_NUMBER, Constants.DEFAULT_SETTLEMENT_ACOOUNT_NUMBER)
//            Prefs.putString(Constants.SETTLEMENT_BANK_CODE, Constants.DEFAULT_SETTLEMENT_BANK_CODE)
//            Prefs.putString(Constants.SETTLEMENT_BANK_NAME, Constants.DEFAULT_SETTLEMENT_BANK_NAME)
//            Prefs.putString(Constants.SETTLEMENT_ACCOUNT_NAME, Constants.DEFAULT_SETTLEMENT_ACCOUNT_NAME)
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