package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillPaymentCategoriesModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.modules.main.billPayment.utils.PackageBottomSheetDialog
import com.interswitchng.smartpos.modules.main.billPayment.utils.BillPaymentSummaryDialog
import com.interswitchng.smartpos.modules.main.transfer.makeActive
import com.interswitchng.smartpos.modules.main.transfer.makeInActive
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_data_plan_input.*

class DataPlanFragment : BaseFragment(TAG), NetworkListCallBackListener<BillPaymentCategoriesModel> {

    lateinit var sheet: PackageBottomSheetDialog
    lateinit var phoneNumber: EditText
    lateinit var packageName: EditText
    lateinit var selectedPackage: BillPaymentCategoriesModel
    lateinit var submitButton: Button
    var isValid = false

    override val layoutId: Int
        get() = R.layout.isw_fragment_data_plan_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumber = isw_dataplan_input_phone
        packageName = isw_data_package_input
        submitButton = isw_dataplan_input_proceed

//        sheet = PackageBottomSheetDialog(this)
//        isw_data_package_input.setOnClickListener {
//            fragmentManager?.let { it1 ->
//                sheet.show(it1, "Bottomet sheet")
//            }
//        }
        setUpPage()
        observeClicks()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = DataPlanFragment()
        private val TAG = "Airtime Recharge Fragment"
    }


    fun observeClicks() {
        submitButton.setOnClickListener {
            submitDetails()
        }
        phoneNumber.addTextChangedListener(object: TextWatcher {
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

        submitButton.makeInActive()
        phoneNumber.error = "Phone number field is required"
        packageName.error = "Package field is required"
    }

    fun validateData() {
        if(phoneNumber.text.toString() == "" || phoneNumber.text.toString() == null){
            isValid = false
            println("Amount is not valid")
            return
        }
        if(!this::selectedPackage.isInitialized){
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
            // Open summary Modal
            showRechargeSummary()
        }
    }

    fun showRechargeSummary() {
//        fragmentManager.let { it1 -> BillPaymentSummaryDialog().show(it1!!, "Summary Dialog Show") }
    }

    override fun onDataReceived(data: BillPaymentCategoriesModel) {
        packageName.setText(data.title)
        selectedPackage = data
        packageName.error = null
        sheet.dismiss()
    }
}