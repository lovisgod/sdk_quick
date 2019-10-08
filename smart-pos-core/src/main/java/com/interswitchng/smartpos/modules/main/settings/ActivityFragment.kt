package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.adapters.ActivityAdapter
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_activity_home_new.*

class ActivityFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_activity_home_new

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
    }

    private fun configureRecyclerView() {
        activity_rv.adapter = ActivityAdapter()
        activity_rv.layoutManager = LinearLayoutManager(context)
    }

    private fun handleToolbarClicks() {
        isw_activity_smart_pos_toolbar.setOnClickListener {
            navigateUp()
        }
    }

    companion object {
        const val TAG = "ACTIVITY FRAGMENT"
    }
}
