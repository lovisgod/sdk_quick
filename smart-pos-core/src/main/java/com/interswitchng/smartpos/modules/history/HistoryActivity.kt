package com.interswitchng.smartpos.modules.history

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.interswitchng.smartpos.R
import kotlinx.android.synthetic.main.isw_activity_history.*
import org.koin.android.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {

    private val historyViewModel: HistoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_history)


        // setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // create paging adapter
        val adapter = TransactionLogAdapter()


        // observe view model
        with(historyViewModel) {

            val owner = { lifecycle }

            loadingState.observe(owner) {
                adapter.setLoadingStatus(it)
            }

            pagedList.observe(owner) {
                adapter.submitList(it)
            }

        }

        // setup recycler view
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = adapter
        rvTransactions.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }
}
