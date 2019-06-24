package com.interswitchng.smartpos.modules.history

import android.arch.paging.PagedList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.transaction.TransactionLog
import kotlinx.android.synthetic.main.isw_activity_history.*
import org.koin.android.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {

    private val historyViewModel: HistoryViewModel by viewModel()
    private lateinit var adapter: TransactionLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_history)


        // setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // create paging adapter
        adapter = TransactionLogAdapter()


        // observe view model
        with(historyViewModel) {

            val owner = { lifecycle }

            loadingState.observe(owner) {
                adapter.setLoadingStatus(it)
            }

            // observe list updates
            pagedList.observe(owner, ::submitList)

        }

        // setup recycler view
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = adapter
        rvTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }




    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // set back click to go back
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun submitList(list: PagedList<TransactionLog>?){
        // hide progress bar if visible
        if (initialProgress.visibility == View.VISIBLE)
            initialProgress.visibility = View.GONE

        if (list != null && list.isEmpty() && adapter.itemCount == 0) {
            rvTransactions.visibility = View.GONE
            tvNoHistory.visibility = View.VISIBLE
        } else {
            adapter.submitList(list)
            rvTransactions.visibility = View.VISIBLE
            tvNoHistory.visibility = View.GONE
        }
    }
}
