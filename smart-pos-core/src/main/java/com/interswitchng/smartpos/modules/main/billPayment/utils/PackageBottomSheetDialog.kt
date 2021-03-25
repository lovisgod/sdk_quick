package com.interswitchng.smartpos.modules.main.billPayment.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.adapters.PackageRecyclerAdapter
import com.interswitchng.smartpos.modules.main.billPayment.models.BillPaymentCategoriesModel
import com.interswitchng.smartpos.modules.main.billPayment.models.NetworkListCallBackListener
import kotlinx.android.synthetic.main.isw_fragment_airtime_recharge_input.*
import kotlinx.android.synthetic.main.isw_package_select_data_bottomsheet.*

class PackageBottomSheetDialog(private val callBackListener: NetworkListCallBackListener<BillPaymentCategoriesModel>): BottomSheetDialogFragment(), NetworkListCallBackListener<BillPaymentCategoriesModel> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ISWPackageCustomShapeAppearanceBottomSheetDialog)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.isw_package_select_data_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = search_view

        val icon = context?.let { AppCompatResources.getDrawable(it, R.drawable.isw_ic_close) }
        if (icon != null) {
            val wraped = DrawableCompat.wrap(icon)
            DrawableCompat.setTint(wraped, Color.WHITE)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val categories = arrayListOf(
                BillPaymentCategoriesModel("Mtn Hynet flex","Flex 3,000", "1"),
                BillPaymentCategoriesModel("Double stuff","Flex 3,000", "2"),
                BillPaymentCategoriesModel("Maiden something","Flex 3,000", "3")
        )
        val recyclerView = isw_package_list_dataplan
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        val adapter = PackageRecyclerAdapter(categories, this)
        recyclerView.adapter = adapter
        val search = (search_view as SearchView)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.getFilter()?.filter(p0)
                return false
            }

        })

    }

    override fun onDataReceived(data: BillPaymentCategoriesModel) {
        callBackListener.onDataReceived(data)

    }
}