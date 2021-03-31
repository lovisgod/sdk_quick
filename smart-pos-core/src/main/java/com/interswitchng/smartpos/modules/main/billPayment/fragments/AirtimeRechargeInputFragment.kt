package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.adapters.NetworkRecyclerAdapter
import com.interswitchng.smartpos.modules.main.billPayment.models.*
import com.interswitchng.smartpos.modules.main.billPayment.utils.BillPaymentSummaryDialog
import com.interswitchng.smartpos.modules.main.models.BillPaymentModel
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.hideKeyboard
import com.interswitchng.smartpos.modules.main.transfer.makeActive
import com.interswitchng.smartpos.modules.main.transfer.makeInActive
import com.interswitchng.smartpos.shared.Constants.NETWORKS_LIST
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_airtime_recharge_input.*
import java.text.DecimalFormat
import java.text.NumberFormat

class AirtimeRechargeInputFragment : BaseFragment(TAG), NetworkListCallBackListener<NetworksModel>, DialogCallBackListener<Boolean> {
    val networks = NETWORKS_LIST

    lateinit var selectedNetwork: NetworksModel
    lateinit var phoneInput: EditText
    lateinit var amountInput: EditText
    lateinit var submitButton: Button
    var isValid = false

    override val layoutId: Int
        get() = R.layout.isw_fragment_airtime_recharge_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amountInput = isw_recharge_amount_input
        phoneInput = isw_recharge_input_phone
        submitButton = isw_recharge_input_proceed

        loadNetworkList()
        setUpPage()
        observeClicks()

    }

    @SuppressLint("WrongConstant")
    fun loadNetworkList() {
        val recyclerView = isw_recharge_networks_list
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        val adapter = NetworkRecyclerAdapter(networks, this)
        recyclerView.adapter = adapter
    }

    fun observeClicks() {
        submitButton.setOnClickListener {
            submitDetails()
        }
        backImg.setOnClickListener {
            navigateUp()
        }
        phoneInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                print("we gather dey")
                validateData()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        amountInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                print("we gather dey")
                validateData()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun setUpPage() {
//        Todo: setup the listeners for
        submitButton.makeInActive()
        amountInput.error = "Amount field is required"
        phoneInput.error = "Phone number field is required"
    }

    fun validateData() {
        if(amountInput.text.toString() == "" || amountInput.text.toString() == null){
            isValid = false
            return
        }
        if(phoneInput.text.toString() == "" || amountInput.text == null){
            isValid = false
            return
        }
        if(!this::selectedNetwork.isInitialized){
            isValid = false
            return
        }
        isValid = true
        submitButton.makeActive()
        submitButton.isEnabled = true
    }

    fun submitDetails() {
        if (isValid){
            val data = AirtimeRechargeModel(
                    selectedNetwork,
                    (amountInput.text.toString().toInt() * 100).toString(),
                    phoneInput.text.toString()
            )

        // Open summary Modal
            showRechargeSummary()
        }
    }

    fun showRechargeSummary() {
        val formatter: NumberFormat = DecimalFormat("#,###")
        val myNumber = amountInput.text.toString().toDouble()
        val formattedNumber: String = formatter.format(myNumber)
        val summary = BillSummaryModel(
                selectedNetwork.networkLogoPath,
                "You are about to make an airtime purchase. Kindly confirm the details below",
                arrayListOf(
                        BillDisplayDataModel("Amount", "NGN $formattedNumber"),
                        BillDisplayDataModel("Phone Number", phoneInput.text.toString())
                )
        )
        fragmentManager.let { it1 -> BillPaymentSummaryDialog(summary, this).show(it1!!, "Summary Dialog Show") }
    }

    override fun onDataReceived(data: NetworksModel) {
        selectedNetwork = data
        phoneInput.hideKeyboard()
        validateData()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AirtimeRechargeInputFragment()
        val TAG = "AirtimeRechargeInput"
    }

    override fun onDialogDataReceived(goForward: Boolean) {
        //  move to the input card fragment
        if (goForward) {
            val payment = PaymentModel()
            payment.type = PaymentModel.TransactionType.BILL_PAYMENT
            payment.amount =   (amountInput.text.toString().toInt() * 100)
            val billPaymentModel = BillPaymentModel()
            billPaymentModel.apply {
                customerId = phoneInput.text.toString()
                phoneNumber = phoneInput.text.toString()
                paymentCode = selectedNetwork.productcode
            }
            payment.billPayment = billPaymentModel
            val action = AirtimeRechargeInputFragmentDirections.iswActionIswAirtimerechargeinputfragmentToIswTransfercardtransactionfragment(payment)
//            val action = AirtimeRechargeInputFragmentDirections.iswActionIswAirtimerechargeinputfragmentToIswBillpaymentcardfragment(payment)
            findNavController().navigate(action)
        }
    }


}