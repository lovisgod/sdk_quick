package com.interswitchng.interswitchpossdk.modules.home

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.isw_activity_home.*
import kotlinx.android.synthetic.main.isw_content_home.*


class HomeActivity : AppCompatActivity() {

    private lateinit var optionsDialog: BottomSheetOptionsDialog

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_home)


        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)
        // set up the UI
        optionsDialog = BottomSheetOptionsDialog.newInstance(info = paymentInfo)

        // setup the drawer layout
        setupDrawerLayout()

        // click listener
        makePurchase.setOnClickListener { optionsDialog.show(supportFragmentManager, optionsDialog.tag) }
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)

    }
}

