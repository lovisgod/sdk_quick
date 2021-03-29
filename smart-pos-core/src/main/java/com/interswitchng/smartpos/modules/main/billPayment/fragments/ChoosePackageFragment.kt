package com.interswitchng.smartpos.modules.main.billPayment.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.isw_fragment_choose_package.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class ChoosePackageFragment : BaseFragment(TAG), NetworkListCallBackListener<BillPaymentCategoriesModel>, DialogCallBackListener<Boolean> {

    val viewmodel:BillPaymentViewmodel by viewModel()
    private val parameters by navArgs<ChoosePackageFragmentArgs>()
    private val billerName by lazy { parameters.billerName }
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
        isw_transfer_input_proceed.makeInActive()
        if (!billerName.isNullOrEmpty()) {
            isw_merchant_name.text = billerName.toString()
        }
        isw_select_package.isClickable = true
        isw_select_package.setOnClickListener {
            viewmodel.getDstvPackages().observe(viewLifecycleOwner, Observer {array ->
                println(array)
                it.let {
                    fragmentManager?.let {
                        dialog = PackageBottomSheetDialog(categories = array, callBackListener = this)
                        dialog.show(it, TAG)

                    }
                }
            })
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
        changeListener(arrayListOf(isw_biller_code, isw_amount, isw_select_package, isw_smart_card_number)) { validateData() }
    }


    fun validateData() {
        if(isw_biller_code.getTextValue().isNullOrEmpty() ||
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
            payment.amount = isw_amount.getTextValue().toInt()
            payment.billPayment = BillPaymentModel()
            payment.billPayment?.customerId = isw_smart_card_number.getTextValue()
            payment.billPayment?.phoneNumber = ""
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