package com.interswitchng.smartpos.modules.main.billPayment.fragments


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.models.BillDisplayDataModel
import com.interswitchng.smartpos.modules.main.billPayment.models.BillPaymentCategoriesModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.modules.main.billPayment.utils.PackageBottomSheetDialog
import com.interswitchng.smartpos.modules.main.billPayment.viewmodels.BillPaymentViewmodel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_choose_package.*
import org.koin.android.viewmodel.ext.android.viewModel


class ChoosePackageFragment : BaseFragment(TAG), NetworkListCallBackListener<BillPaymentCategoriesModel> {

    val viewmodel:BillPaymentViewmodel by viewModel()
    private val parameters by navArgs<ChoosePackageFragmentArgs>()
    private val billerName by lazy { parameters.billerName }

    override val layoutId: Int
        get() = R.layout.isw_fragment_choose_package

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        if (!billerName.isNullOrEmpty()) {
            isw_merchant_name.text = billerName.toString()
        }
        isw_select_package.isClickable = true
        isw_select_package.setOnClickListener {
            viewmodel.getDstvPackages().observe(viewLifecycleOwner, Observer {array ->
                println(array)
                it.let {
                    fragmentManager?.let {
                        PackageBottomSheetDialog(categories = array, callBackListener = this).show(it, TAG)

                    }
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChoosePackageFragment()
        val TAG = "CHOOSE PACKAGE FRAGMENT"
    }

    override fun onDataReceived(data: BillPaymentCategoriesModel) {
        println(data)
        if (!data.productcode.isNullOrEmpty()) {
            isw_biller_code.setText(data.productcode.toString())
            isw_select_package.setText(data.title)
        } else {
            isw_biller_code.isFocusable = true
        }
    }


}