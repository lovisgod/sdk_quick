package com.interswitchng.smartpos.modules.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.utils.IswCompositeDisposable
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import kotlinx.android.synthetic.main.isw_activity_settings.*
import org.koin.android.ext.android.inject
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private val store: KeyValueStore by inject()
    private val isoService: IsoService by inject()

    private val disposables = IswCompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        // set the text values
        setupTexts()
        // setup button listeners
        setupButtons()
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
                tvKeys.text = getString(R.string.isw_title_downloading_keys)
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
                tvTerminalInfo.text = getString(R.string.isw_title_downloading_terminal_config)
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
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, dateStr)
            tvTerminalInfo.text = getString(R.string.isw_title_terminal_config_downloaded)
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
            tvTerminalInfo.text = getString(R.string.isw_title_download_terminal_configuration)
        }

        if (keysDate != -1L) {
            val date = Date(terminalDate)
            val dateStr = DisplayUtils.getIsoString(date)
            tvKeyDate.text = getString(R.string.isw_title_date, dateStr)
            tvKeys.text = getString(R.string.isw_title_keys_downloaded)
        } else {
            val message = "No keys"
            tvKeyDate.text = getString(R.string.isw_title_date, message)
            tvKeys.text = getString(R.string.isw_title_download_keys)
        }
    }


    private fun downloadKeys(terminalId: String) {
        val disposable = ThreadUtils.createExecutor {
            // download keys and update ui
            val isSuccessful = isoService.downloadKey(terminalId)
            runOnUiThread { keysDownloaded(isSuccessful) }
        }

        disposables.add(disposable)
    }

    private fun downloadTerminalConfig(terminalId: String) {
        val disposable = ThreadUtils.createExecutor {
            val isSuccessful = isoService.downloadTerminalParameters(terminalId)
            runOnUiThread {
                terminalConfigDownloaded(isSuccessful)
            }
        }

        disposables.add(disposable)
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
            btnDownloadKeys.setColorFilter(android.R.color.holo_green_dark)
        } else {
            val message = "No keys downloaded"
            tvKeyDate.text = getString(R.string.isw_title_date, message)
            tvKeys.text = getString(R.string.isw_title_error_downloading_keys)

            // set the drawable and color
            btnDownloadKeys.setImageResource(R.drawable.isw_ic_error)
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
            store.saveNumber(KEY_DATE_TERMINAL, date.time)

            // set the texts
            val dateStr = DisplayUtils.getIsoString(date)
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, dateStr)
            tvTerminalInfo.text = getString(R.string.isw_title_terminal_config_downloaded)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_check)
            btnDownloadTerminalConfig.setColorFilter(android.R.color.holo_green_dark)
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
            tvTerminalInfo.text = getString(R.string.isw_title_error_downloading_terminal_config)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_warning)
            btnDownloadTerminalConfig.setColorFilter(android.R.color.holo_red_dark)
        }
    }

    companion object {
        const val KEY_DATE_TERMINAL = "key_download_terminal_date"
        const val KEY_DATE_KEYS = "key_download_key_date"
    }
}