package com.interswitchng.interswitchpossdk.modules.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IsoService
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.inject
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private val store: IKeyValueStore by inject()
    private val isoService: IsoService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun onStart() {
        super.onStart()

        // set the text values
        setupTexts()
        // setup button listeners
        setupButtons()
    }

    private fun setupButtons() {

        // set up buttons
        btnDownloadKeys.setOnClickListener {
            // validate that a terminal id is present
            val terminalID: String = terminalId.text.toString()

            if (terminalID.isEmpty() || terminalID.length != 8) {
                // validate terminal id
                Toast.makeText(this, "Input a valid terminal Id", Toast.LENGTH_LONG).show()
            } else {
                // disable and hide button
                btnDownloadKeys.isEnabled = false
                btnDownloadKeys.visibility = View.GONE

                // set the text of keys
                tvKeys.text = getString(R.string.title_downloading_keys)
                // show progress bar
                progressKeyDownload.visibility = View.VISIBLE
                // hide download date
                tvKeyDate.visibility = View.GONE

                // trigger download keys
                downloadKeys(terminalID)
            }
        }

        btnDownloadTerminalConfig.setOnClickListener {
            // validate that a terminal id is present
            val terminalID: String = terminalId.text.toString()

            // validate terminal id
            if (terminalID.isEmpty() || terminalID.length != 8) {
                Toast.makeText(this, "Input a valid terminal Id", Toast.LENGTH_LONG).show()
            } else {
                // disable and hide button
                btnDownloadTerminalConfig.isEnabled = false
                btnDownloadTerminalConfig.visibility = View.GONE

                // set the text of terminal config
                tvTerminalInfo.text = getString(R.string.title_downloading_terminal_config)
                // show progress bar
                progressTerminalDownload.visibility = View.VISIBLE
                // hide download date
                tvTerminalInfoDate.visibility = View.GONE

                // trigger download terminal config
                downloadTerminalConfig(terminalID)
            }
        }
    }

    private fun setupTexts() {
        val terminalDate = store.getNumber(KEY_DATE_TERMINAL, -1)
        val keysDate = store.getNumber(KEY_DATE_KEYS, -1)

        if (terminalDate != -1L) {
            val date = Date(terminalDate)
            val dateStr = DisplayUtils.getIsoString(date)
            tvTerminalInfoDate.text = getString(R.string.title_date, dateStr)
            tvTerminalInfo.text = getString(R.string.title_terminal_config_downloaded)
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.title_date, message)
            tvTerminalInfo.text = getString(R.string.title_download_terminal_configuration)
        }

        if (keysDate != -1L) {
            val date = Date(terminalDate)
            val dateStr = DisplayUtils.getIsoString(date)
            tvKeyDate.text = getString(R.string.title_date, dateStr)
            tvKeys.text = getString(R.string.title_keys_downloaded)
        } else {
            val message = "No keys"
            tvKeyDate.text = getString(R.string.title_date, message)
            tvKeys.text = getString(R.string.title_download_keys)
        }
    }


    private fun downloadKeys(terminalId: String) {
        Thread {
            // download keys and update ui
            val isSuccessful = isoService.downloadKey(terminalId)
            runOnUiThread {
                keysDownloaded(isSuccessful)
            }
        }.start()
    }

    private fun downloadTerminalConfig(terminalId: String) {
        Thread {
            val isSuccessful = isoService.downloadTerminalParameters(terminalId)
            runOnUiThread {
                terminalConfigDownloaded(isSuccessful)
            }
        }.start()
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
            store.getNumber(KEY_DATE_KEYS, date.time)

            // set the texts
            val dateStr = DisplayUtils.getIsoString(date)
            tvKeyDate.text = getString(R.string.title_date, dateStr)
            tvKeys.text = getString(R.string.title_keys_downloaded)

            // set the drawable and color
            btnDownloadKeys.setImageResource(R.drawable.ic_check)
            btnDownloadKeys.setColorFilter(android.R.color.holo_green_dark)
        } else {
            val message = "No keys downloaded"
            tvKeyDate.text = getString(R.string.title_date, message)
            tvKeys.text = getString(R.string.title_error_downloading_keys)

            // set the drawable and color
            btnDownloadKeys.setImageResource(R.drawable.ic_error)
            btnDownloadKeys.setColorFilter(android.R.color.holo_red_dark)
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
            store.getNumber(KEY_DATE_TERMINAL, date.time)

            // set the texts
            val dateStr = DisplayUtils.getIsoString(date)
            tvTerminalInfoDate.text = getString(R.string.title_date, dateStr)
            tvTerminalInfo.text = getString(R.string.title_terminal_config_downloaded)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.ic_check)
            btnDownloadTerminalConfig.setColorFilter(android.R.color.holo_green_dark)
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.title_date, message)
            tvTerminalInfo.text = getString(R.string.title_error_downloading_terminal_config)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.ic_warning)
            btnDownloadTerminalConfig.setColorFilter(android.R.color.holo_red_dark)
        }
    }

    companion object {
        const val KEY_DATE_TERMINAL = "key_download_terminal_date"
        const val KEY_DATE_KEYS = "key_download_key_date"
    }
}