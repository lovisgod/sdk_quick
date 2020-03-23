package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.adapters.ActivityAdapter
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.modules.menu.history.HistoryViewModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import kotlinx.android.synthetic.main.isw_activity_home_new.*
import org.koin.android.viewmodel.ext.android.viewModel

class ActivityFragment : BaseFragment(TAG), ActivityAdapter.ActivityItemClickListener {

    override val layoutId: Int
        get() = R.layout.isw_activity_home_new

    private val historyViewModel: HistoryViewModel by viewModel()
    private lateinit var adapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.isw_activity_home_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
        handleToolbarClicks()
        observeHistoryViewModel()
    }

    private fun configureRecyclerView() {
        adapter = ActivityAdapter().apply {
            setOnActivityItemClickListener(this@ActivityFragment)
        }
        activity_rv.adapter = adapter
        activity_rv.layoutManager = LinearLayoutManager(context)
    }

    private fun observeHistoryViewModel() {
        with(historyViewModel) {
            val owner = { lifecycle }
            pagedList.observe(owner, ::submitList)
        }
    }

    private fun submitList(list: PagedList<TransactionLog>?){

        if (list != null && list.isEmpty() && adapter.itemCount == 0) {

        } else {
            adapter.submitList(list)
        }
    }

    private fun handleToolbarClicks() {
        isw_activity_smart_pos_toolbar.setOnClickListener {
            navigateUp()
        }
    }

    override fun navigateToActivityDetailFragment(item: TransactionLog, transactionResponseModel: TransactionResponseModel) {
        val direction = ActivityFragmentDirections.iswGotoActivityDetailFragmentAction(item, transactionResponseModel)
        navigate(direction)
    }

    companion object {
        const val TAG = "ACTIVITY FRAGMENT"
    }
}
