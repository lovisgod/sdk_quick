package com.interswitchng.smartpos.modules.menu.settings

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.MenuActivity
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.isw_activity_settings.*
import kotlinx.android.synthetic.main.isw_activity_settings.toolbar
import kotlinx.android.synthetic.main.isw_activity_settlement_selection_settings.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class SettlementSelectionActivity : MenuActivity() {

    private val store: KeyValueStore by inject()

protected val iswPos: IswPos by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_settlement_selection_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        handleClicks()


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // set back click to go back
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    private fun handleClicks() {
        iswKimonoSettings.setOnClickListener {

            TerminalInfo.setSettingsSettlementChoice(true,store)
            iswPos.gotoSettings()
        }


        iswNibssSettings.setOnClickListener {
            TerminalInfo.setSettingsSettlementChoice(false,store)
            iswPos.gotoSettings()
        }


    }

    companion object {
        const val TAG = "Setting Settlement Selection Fragment"
    }
}