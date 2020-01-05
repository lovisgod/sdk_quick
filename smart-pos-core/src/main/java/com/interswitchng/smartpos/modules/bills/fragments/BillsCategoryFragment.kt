package com.interswitchng.smartpos.modules.bills.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.adapters.BillsCategoryAdapter
import com.interswitchng.smartpos.modules.bills.models.BillCategoryModel
import com.interswitchng.smartpos.modules.bills.models.BillModel
import com.interswitchng.smartpos.modules.main.MainActivity
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_bills.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class BillsCategoryFragment : BaseFragment(TAG), BillsCategoryAdapter.BillCategoryItemClickListener {


    override fun navigateToBillsFragment(item: BillCategoryModel) {
        val direction = BillsCategoryFragmentDirections.iswActionIswBillscategoryfragment2ToIswBillfragment(item)
        navigate(direction)
    }

    override val layoutId: Int
        get() = R.layout.isw_fragment_bills_categories


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initializeViews()
    }

    private fun initializeViews() {
        bills_rv.layoutManager = LinearLayoutManager(context)
        bills_rv.adapter = BillsCategoryAdapter(getBillCategories(), context, this)

        backImg.setOnClickListener {
            navigateUp()
        }
    }

    private fun getBillCategories(): ArrayList<BillCategoryModel> {
        val categories: ArrayList<BillCategoryModel> = ArrayList()

        var category1 = BillCategoryModel()
        category1.id = 1.toString()
        category1.name = "Airtime and Data"
        category1.bills = generateBillItems(category1, 10)

        var category2 = BillCategoryModel()
        category2.id = 2.toString()
        category2.name = "Cable Tv"
        category2.bills = generateBillItems(category2, 4)

        var category3 = BillCategoryModel()
        category3.id = 3.toString()
        category3.name = "School and Exam Fees"
        category3.bills = generateBillItems(category3, 6)

        var category4 = BillCategoryModel()
        category4.id = 4.toString()
        category4.name = "Mobile Money Wallet"
        category4.bills = generateBillItems(category4, 6)

        categories.add(category1)
        categories.add(category2)
        categories.add(category3)
        categories.add(category4)

        return categories
    }

    private fun generateBillItems(nameOfCategory: BillCategoryModel, number: Int): ArrayList<BillModel> {
        val bills: ArrayList<BillModel> = ArrayList()

        for (x in 0..number) {
            val bill = BillModel()
            bill.id = x.toString()
            bill.name = nameOfCategory.name + "_" + x.toString()
            bills.add(bill)
        }

        return bills
    }


    companion object {
        fun newInstance(): BillsCategoryFragment {
            return BillsCategoryFragment()
        }

        const val TAG = "BILL CATEGORIES FRAGMENT"
    }
}
