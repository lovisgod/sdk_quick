package com.interswitchng.smartpos.shared.activities

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

abstract class MenuActivity: AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // set back click to go back
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}