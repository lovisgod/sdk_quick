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
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.MenuActivity
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.isw_activity_settings.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class SettingsActivity : MenuActivity() {

    private val store: KeyValueStore by inject()
    private val settingsViewModel: SettingsViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        // set the text values
        setupTexts()
        // setup button listeners
        setupButtons()

        // observe view model
        with(settingsViewModel) {
            val owner = { lifecycle }

            // observe key download status
            keysDownloadSuccess.observe(owner) {
                it?.apply(::keysDownloaded)
            }

            // observe config download status
            configDownloadSuccess.observe(owner) {
                it?.apply(::terminalConfigDownloaded)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // set back click to go back
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupButtons() {


        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, listOf("Nibss", "Kimono"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        settlementTypeSpinner.adapter = adapter
        settlementTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // either one will work as well
                // val item = parent.getItemAtPosition(position) as String
                val item = adapter.getItem(position)



            }
        }


        // set up buttons
        btnDownloadKeys.setOnClickListener {
            // validate that a terminal id is present
            val terminalID: String = etTerminalId.text.toString()

            if (terminalID.isEmpty() || terminalID.length != 8) {
                // validate terminal id
                Toast.makeText(this, "Input a valid terminal Id", Toast.LENGTH_LONG).show()
            } else {
                // disable and hide button
                btnDownloadKeys.isEnabled = false
                btnDownloadKeys.visibility = View.GONE

                // set the text of keys
                tvKeys.text = getString(R.string.isw_title_downloading_keys)
                // show progress bar
                progressKeyDownload.visibility = View.VISIBLE
                // hide download date
                tvKeyDate.visibility = View.GONE

                // save terminal id
                store.saveString(KEY_TERMINAL_ID, terminalID)


                //TODO get the IP and Port for ISW
                // trigger download keys
                settingsViewModel.downloadKeys(terminalID)
            }
        }

        btnDownloadTerminalConfig.setOnClickListener {
            // validate that a terminal id is present
            val terminalID: String = etTerminalId.text.toString()

            // validate terminal id
            if (terminalID.isEmpty() || terminalID.length != 8) {
                Toast.makeText(this, "Input a valid terminal Id", Toast.LENGTH_LONG).show()
            } else {
                // disable and hide button
                btnDownloadTerminalConfig.isEnabled = false
                btnDownloadTerminalConfig.visibility = View.GONE

                // set the text of terminal config
                tvTerminalInfo.text = getString(R.string.isw_title_downloading_terminal_config)
                // show progress bar
                progressTerminalDownload.visibility = View.VISIBLE
                // hide download date
                tvTerminalInfoDate.visibility = View.GONE


                settingsViewModel.downloadTerminalConfig(terminalID)

//                var position =  settlementTypeSpinner.selectedItemPosition
//                if (position==0)
//                    store.saveString(Constants.TERMINAL_CONFIG_TYPE,"nibss")
//                else if(position==1)
//                    store.saveString(Constants.TERMINAL_CONFIG_TYPE,"kimono")
            }
        }
    }

    private fun setupTexts() {
        val terminalDate = store.getNumber(KEY_DATE_TERMINAL, -1)
        val terminalId = store.getString(KEY_TERMINAL_ID, "")
        val keysDate = store.getNumber(KEY_DATE_KEYS, -1)

        // set the value of terminal
        etTerminalId.setText(terminalId)

        var selectedSettlement =store.getString(Constants.TERMINAL_CONFIG_TYPE,"kimono")


        if (selectedSettlement=="kimono"){

        }
        else {

        }


        if (terminalDate != -1L) {
            val date = Date(terminalDate)
            val dateStr = DateUtils.timeOfDateFormat.format(date)
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, dateStr)
            tvTerminalInfo.text = getString(R.string.isw_title_terminal_config_downloaded)
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
            tvTerminalInfo.text = getString(R.string.isw_title_download_terminal_configuration)
        }

        if (keysDate != -1L) {
            val date = Date(terminalDate)
            val dateStr = DateUtils.timeOfDateFormat.format(date)
            tvKeyDate.text = getString(R.string.isw_title_date, dateStr)
            tvKeys.text = getString(R.string.isw_title_keys_downloaded)
        } else {
            val message = "No keys"
            tvKeyDate.text = getString(R.string.isw_title_date, message)
            tvKeys.text = getString(R.string.isw_title_download_keys)
        }
    }


    private fun keysDownloaded(isSuccessful: Boolean) {
        // enable and show button
        btnDownloadKeys.isEnabled = true
        btnDownloadKeys.visibility = View.VISIBLE
        // hide progress bar
        progressKeyDownload.visibility = View.GONE
        // show download date
        tvKeyDate.visibility = View.VISIBLE


        if (isSuccessful) {
            // get and store date
            val date = Date()
            store.saveNumber(KEY_DATE_KEYS, date.time)

            // set the texts
            val dateStr = DisplayUtils.getIsoString(date)
            tvKeyDate.text = getString(R.string.isw_title_date, dateStr)
            tvKeys.text = getString(R.string.isw_title_keys_downloaded)

            // set the drawable and color
            btnDownloadKeys.setImageResource(R.drawable.isw_ic_check)
            val color = ContextCompat.getColor(this, R.color.iswTextColorSuccessDark)
            ImageViewCompat.setImageTintList(btnDownloadKeys, ColorStateList.valueOf(color))
        } else {
            val message = "No keys downloaded"
            tvKeyDate.text = getString(R.string.isw_title_date, message)
            tvKeys.text = getString(R.string.isw_title_error_downloading_keys)

            // set the drawable and color
            btnDownloadKeys.setImageResource(R.drawable.isw_ic_error)
            val color = ContextCompat.getColor(this, R.color.iswTextColorError)
            ImageViewCompat.setImageTintList(btnDownloadKeys, ColorStateList.valueOf(color))
        }

    }

    private fun terminalConfigDownloaded(isSuccessful: Boolean) {
        // enable and show button
        btnDownloadTerminalConfig.isEnabled = true
        btnDownloadTerminalConfig.visibility = View.VISIBLE
        // hide progress bar
        progressTerminalDownload.visibility = View.GONE
        // show download date
        tvTerminalInfoDate.visibility = View.VISIBLE


        if (isSuccessful) {
            // get and store date
            val date = Date()
            store.saveNumber(KEY_DATE_TERMINAL, date.time)

            // set the texts
            val dateStr = DisplayUtils.getIsoString(date)
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, dateStr)
            tvTerminalInfo.text = getString(R.string.isw_title_terminal_config_downloaded)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_check)
            val color = ContextCompat.getColor(this, R.color.iswTextColorSuccessDark)
            ImageViewCompat.setImageTintList(btnDownloadTerminalConfig, ColorStateList.valueOf(color))
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
            tvTerminalInfo.text = getString(R.string.isw_title_error_downloading_terminal_config)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_error)
            val color = ContextCompat.getColor(this, R.color.iswTextColorError)
            ImageViewCompat.setImageTintList(btnDownloadTerminalConfig, ColorStateList.valueOf(color))
        }
    }

    companion object {
        const val KEY_DATE_TERMINAL = "key_download_terminal_date"
        const val KEY_DATE_KEYS = "key_download_key_date"
        const val KEY_TERMINAL_ID = "key_terminal_id"
    }
}