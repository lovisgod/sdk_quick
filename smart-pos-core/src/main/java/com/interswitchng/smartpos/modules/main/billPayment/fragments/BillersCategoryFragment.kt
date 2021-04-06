package com.interswitchng.smartpos.modules.main.billPayment.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.adapters.cableTvBillerCategoryAdapter
import com.interswitchng.smartpos.modules.main.billPayment.models.BillDisplayDataModel
import com.interswitchng.smartpos.modules.main.billPayment.models.ChoosePackageConfigModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import com.interswitchng.smartpos.modules.main.billPayment.viewmodels.BillPaymentViewmodel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.isw_fragment_billers_category.*
import kotlinx.android.synthetic.main.isw_fragment_choose_category.*
import org.koin.android.viewmodel.ext.android.viewModel


class BillersCategoryFragment : DialogFragment(), NetworkListCallBackListener<BillDisplayDataModel> {
   private var list = ArrayList<BillDisplayDataModel>()
   private lateinit var adapter: cableTvBillerCategoryAdapter
   private lateinit var packageChoiceConfig: ChoosePackageConfigModel

   private val viewmodel: BillPaymentViewmodel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        getData()
    }

    override fun getTheme(): Int {
        return R.style.ISW_FullScreenDialogStyle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false
        super.onCreateView(inflater, container, savedInstanceState)
        var rootView: View = inflater.inflate(R.layout.isw_fragment_billers_category, container, false)
        return rootView
    }

    private fun setupUI() {
        adapter = cableTvBillerCategoryAdapter(callBackListener = this)
        isw_biller_providers.adapter = adapter
        isw_biller_providers.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)

        isw_transfer_bank_search_bar.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.getFilter()?.filter(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        back_arrow.setOnClickListener {
            findNavController().popBackStack(R.id.isw_choosecategoryfragment, false)
        }
    }

    private fun getData() {
        packageChoiceConfig = ChoosePackageConfigModel(billerName = "")
        when(Prefs.getString("CATEGORY_CHOSEN", "CABLE")) {
            "CABLE" -> viewmodel.getCableBillers().observe(viewLifecycleOwner, Observer {
                it.let {
                    if (!it.isNullOrEmpty()) {
                        list = it
                        adapter.setData(list)
                    }
                }
            })
            "UTILITY" -> viewmodel.getUtilityBillers().observe(viewLifecycleOwner, Observer {
                packageChoiceConfig.selectPackageField?.show = false
                it.let {
                    if (!it.isNullOrEmpty()) {
                        list = it
                        adapter.setData(list)
                    }
                }
            })
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() = BillersCategoryFragment()
        val TAG = "BillerCategoryFragment"
    }

    override fun onDataReceived(data: BillDisplayDataModel) {
        packageChoiceConfig = viewmodel.getBillerConfig(data)
        val action = BillersCategoryFragmentDirections.iswActionIswBillerscategoryfragmentToIswChoosepackagefragment(billerName = data.title, fieldsConfigModel = packageChoiceConfig)
        findNavController().navigate(action)
    }


}