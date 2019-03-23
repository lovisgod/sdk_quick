package com.interswitchng.smartpos.modules.home

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.isw_activity_home.*
import kotlinx.android.synthetic.main.isw_content_home.*


class HomeActivity : AppCompatActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_home)

        // setup the drawer layout
        setupDrawerLayout()

        // click listener
        makePurchase.setOnClickListener {

        }
    }

    private fun setupDrawerLayout() {

        setSupportActionBar(homeToolbar)

        // Find our drawer view
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, homeToolbar, R.string.isw_drawer_open, R.string.isw_drawer_close);

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle)
    }

    override fun onStart() {
        super.onStart()
        // Sync the toggle state after has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }
}

