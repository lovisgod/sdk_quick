package com.interswitchng.smartpos.modules.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import kotlinx.android.synthetic.main.isw_activity_home.*
import kotlinx.android.synthetic.main.isw_content_home.*
import kotlinx.android.synthetic.main.isw_content_home_drawer.*
import org.koin.android.ext.android.inject


class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private val instance: IswPos by inject()

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_home)

        // setup the drawer layout
        setupDrawerLayout()

        // click listener
        makePurchase.setOnClickListener { startActivity(Intent(this, PurchaseActivity::class.java)) }
    }

    private fun setupDrawerLayout() {

        setSupportActionBar(homeToolbar)

        // Find our drawer view
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, homeToolbar, R.string.isw_drawer_open, R.string.isw_drawer_close);

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle)
        history.setOnClickListener(this)
        reports.setOnClickListener(this)
        settings.setOnClickListener(this)
        merchantInfo.setOnClickListener(this)
        help.setOnClickListener(this)
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


    override fun onClick(view: View) {
        when (view.id) {
            R.id.settings -> instance.gotoSettings()
            R.id.history -> instance.gotoHistory()
            R.id.reports -> instance.gotoReports()
        }

        // hide the drawer
        drawerLayout.closeDrawer(Gravity.START)
    }
}

