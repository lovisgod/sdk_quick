package com.interswitchng.smartpos.modules.main.billPayment.fragments


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.*
import com.interswitchng.smartpos.modules.main.billPayment.utils.BillPaymentSummaryDialog
import com.interswitchng.smartpos.modules.main.billPayment.utils.PackageBottomSheetDialog
import com.interswitchng.smartpos.modules.main.billPayment.viewmodels.BillPaymentViewmodel
import com.interswitchng.smartpos.modules.main.models.BillPaymentModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.*
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_airtime_recharge_input.*
import kotlinx.android.synthetic.main.isw_fragment_choose_package.*
import org.koin.android.viewmodel.ext.android.viewModel


class ChoosePackageFragment : BaseFragment(TAG), NetworkListCallBackListener<BillPaymentCategoriesModel>, DialogCallBackListener<Boolean> {

    val viewmodel:BillPaymentViewmodel by viewModel()
    private val parameters by navArgs<ChoosePackageFragmentArgs>()
    private val billerName by lazy { parameters.billerName }
    private val fieldsConfig by lazy { parameters.fieldsConfigModel}
    private lateinit var dialog: PackageBottomSheetDialog

    var isValid: Boolean = false

    private lateinit var selected: BillPaymentCategoriesModel

    override val layoutId: Int
        get() = R.layout.isw_fragment_choose_package

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observerTextChange()
    }

    private fun setupUI() {
        isw_back.setOnClickListener {
            navigateUp()
        }
        // Check and set based on config
        fieldsConfig.let {
            // Set visibility
            isw_select_package_container.isVisible(it.selectPackageField?.show!!)
            isw_select_package.isVisible(it.selectPackageField?.show!!)
            isw_select_package_label.isVisible(it.selectPackageField?.show!!)

            isw_smart_card_number_label.isVisible(it.accountNumberField?.show!!)
            isw_smart_card_number.isVisible(it.accountNumberField?.show!!)

            isw_amount_container.isVisible(it.amountField?.show!!)
            isw_amount_label.isVisible(it.amountField?.show!!)
            isw_amount.isVisible(it.amountField?.show!!)

            isw_phone_number.isVisible(it.phoneNumberField?.show!!)
            isw_phone_number_label.isVisible(it.phoneNumberField?.show!!)
            isw_phone_number_container.isVisible(it.phoneNumberField?.show!!)

            isw_biller_code_container.isVisible(it.billerCodeField?.show!!)
            isw_biller_code_label.isVisible(it.billerCodeField?.show!!)
            isw_biller_code.isVisible(it.billerCodeField?.show!!)


//            set field properties
            if(it.accountNumberField?.show!!) {
                isw_smart_card_number_label.text = it.accountNumberField.title
            }
            if(it.selectPackageField?.show!!) {
                isw_select_package.isClickable = true
                isw_select_package.setOnClickListener {
                    viewmodel.getDstvPackages(billerName.toString().toLowerCase()).observe(viewLifecycleOwner, Observer {array ->
                        it.let {
                            fragmentManager?.let {
                                dialog = PackageBottomSheetDialog(categories = array, callBackListener = this)
                                dialog.show(it, TAG)

                            }
                        }
                    })
                }
                isw_select_package_label.text = it.selectPackageField.title
            }
            if(it.amountField?.show!!) {
                isw_amount_label.text = it.amountField.title
            }
            if(it.billerCodeField?.show!!){
                isw_biller_code_label.text = it.billerCodeField.title
            }

        }

        isw_transfer_input_proceed.makeInActive()
        if (!billerName.isNullOrEmpty()) {
            isw_merchant_name.text = billerName.toString()
        }


        isw_transfer_input_proceed.setOnClickListener {
            showRechargeSummary()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChoosePackageFragment()
        val TAG = "CHOOSE PACKAGE FRAGMENT"
    }

    override fun onDataReceived(data: BillPaymentCategoriesModel) {
        dialog.dismiss()
        println(data)
        selected = data
        if (!data.productcode.isNullOrEmpty()) {
            isw_biller_code.setText(data.productcode.toString())
            isw_select_package.setText(data.title)
            isw_biller_code.isFocusable = false
        } else {
            isw_select_package.setText(data.title)
            isw_biller_code.isFocusable = true
        }
    }


    fun observerTextChange() {
        changeListener(arrayListOf(isw_biller_code, isw_amount, isw_select_package, isw_smart_card_number, isw_phone_number)) { validateData() }
    }


    fun validateData() {
        if(
                isw_biller_code.getTextValue().isNullOrEmpty() ||
                (isw_phone_number.getTextValue().isNullOrEmpty() && fieldsConfig?.phoneNumberField?.required!!)||
                isw_select_package.getTextValue().isNullOrEmpty() ||
                isw_smart_card_number.getTextValue().isNullOrEmpty() ||
                isw_amount.getTextValue().isNullOrEmpty()){
            isValid = false
            println("Amount is not valid")
            return
        } else {
            isw_transfer_input_proceed.makeActive()
        }

    }

    override fun onDialogDataReceived(data: Boolean) {
        if (data) {
            val payment = PaymentModel()
            payment.type = PaymentModel.TransactionType.BILL_PAYMENT
            payment.amount = isw_amount.getTextValue().toInt() * 100
            payment.billPayment = BillPaymentModel()
            payment.billPayment?.customerId = isw_smart_card_number.getTextValue()
            payment.billPayment?.phoneNumber = isw_phone_number.getTextValue()
            payment.billPayment?.customerEmail = ""
            payment.billPayment?.paymentCode = isw_biller_code.getTextValue()

            val action = ChoosePackageFragmentDirections.iswActionIswChoosepackagefragmentToIswTransfercardtransactionfragment(payment)
            findNavController().navigate(action)
        }
    }

    fun showRechargeSummary() {
        val summary = BillSummaryModel(
                selected.logoPath,
                "You are about to make an ${selected.title} purchase. Kindly confirm the details below",
                arrayListOf(
                        BillDisplayDataModel("Amount", isw_amount.text.toString()),
                        BillDisplayDataModel("${isw_smart_card_number_label.text.toString()}", isw_smart_card_number.text.toString())
                )
        )
        fragmentManager.let { it1 -> BillPaymentSummaryDialog(summary, this).show(it1!!, "Summary Dialog Show") }
    }

}