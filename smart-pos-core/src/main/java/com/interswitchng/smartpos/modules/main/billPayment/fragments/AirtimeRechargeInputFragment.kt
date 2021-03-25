package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.adapters.NetworkRecyclerAdapter
import com.interswitchng.smartpos.modules.main.billPayment.models.AirtimeRechargeModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.modules.main.billPayment.utils.RechargeSummaryDialog
import com.interswitchng.smartpos.modules.main.transfer.makeActive
import com.interswitchng.smartpos.modules.main.transfer.makeInActive
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_airtime_recharge_input.*
import kotlinx.android.synthetic.main.isw_transfer_bank_filter_dialog.*
import java.util.*

class AirtimeRechargeInputFragment : BaseFragment(TAG), NetworkListCallBackListener<String> {
    val networks = arrayOf<String>("MTN", "GLO", "AIRTEL", "9MOBILE")
    var selectedNetwork: String = ""
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
        phoneInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                print("we gather dey")
                validateData()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        amountInput.addTextChangedListener(object: TextWatcher {
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
            println("Amount is not valid")
            return
        }
        if(phoneInput.text.toString() == "" || amountInput.text == null){
            isValid = false
            println("Phone number is not valid")
            return
        }
        if(selectedNetwork == null || selectedNetwork == ""){
            isValid = false
            println("Selected is not valid")
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
                    amountInput.text.toString(),
                    phoneInput.text.toString()
            )

        // Open summary Modal
            showRechargeSummary()
        }
    }

    fun showRechargeSummary() {
        fragmentManager.let { it1 -> RechargeSummaryDialog().show(it1!!, "Summary Dialog Show") }
    }

    override fun onDataReceived(data: String) {
        selectedNetwork = data
        validateData()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AirtimeRechargeInputFragment()
        val TAG = "AirtimeRechargeInput"
    }



}