package com.interswitchng.smartpos.modules.setup.fragments

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class DownloadKeyFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_activity_settings
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // set the text values
//        setupTexts()
//        // setup button listeners
//        setupButtons()
//
//        // observe view model
//        with(settingsViewModel) {
//            val owner = { lifecycle }
//
//            // observe key download status
//            keysDownloadSuccess.observe(owner) {
//                it?.apply(::keysDownloaded)
//            }
//
//            // observe config download status
//            configDownloadSuccess.observe(owner) {
//                it?.apply(::terminalConfigDownloaded)
//            }
//        }
//        isw_keys_next.setOnClickListener {
//            if (TerminalInfo.get(store) == null) {
//                toast("Download terminal configuration to continue!")
//            } else {
//            }
//        }
//    }
//
//    private fun setupButtons() {
//        // set up buttons
//        btnDownloadKeys.setOnClickListener {
//            // validate that a terminal id is present
//            val terminalID: String = etTerminalId.text.toString()
//
//            if (terminalID.isEmpty() || terminalID.length != 8) {
//                // validate terminal id
//                Toast.makeText(requireContext(), "Input a valid terminal Id", Toast.LENGTH_LONG).show()
//            } else {
//                // disable and hide button
//                btnDownloadKeys.isEnabled = false
//                btnDownloadKeys.visibility = View.GONE
//
//                // set the text of keys
//                tvKeys.text = getString(R.string.isw_title_downloading_keys)
//                // show progress bar
//                progressKeyDownload.visibility = View.VISIBLE
//                // hide download date
//                tvKeyDate.visibility = View.GONE
//
//                // save terminal id
//                store.saveString(SettingsActivity.KEY_TERMINAL_ID, terminalID)
//
//                // trigger download keys
////                settingsViewModel.downloadKeys(terminalID)
//            }
//        }
//
//        btnDownloadTerminalConfig.setOnClickListener {
//            // validate that a terminal id is present
//            val terminalID: String = etTerminalId.text.toString()
//
//            // validate terminal id
//            if (terminalID.isEmpty() || terminalID.length != 8) {
//                Toast.makeText(requireContext(), "Input a valid terminal Id", Toast.LENGTH_LONG).show()
//            } else {
//                // disable and hide button
//                btnDownloadTerminalConfig.isEnabled = false
//                btnDownloadTerminalConfig.visibility = View.GONE
//
//                // set the text of terminal config
//                tvTerminalInfo.text = getString(R.string.isw_title_downloading_terminal_config)
//                // show progress bar
//                progressTerminalDownload.visibility = View.VISIBLE
//                // hide download date
//                tvTerminalInfoDate.visibility = View.GONE
//
//                // trigger download terminal config
////                settingsViewModel.downloadTerminalConfig(terminalID)
//            }
//        }
//    }
//
//    private fun setupTexts() {
//        val terminalDate = store.getNumber(KEY_DATE_TERMINAL, -1)
//        val terminalId = store.getString(KEY_TERMINAL_ID, "")
//        val keysDate = store.getNumber(KEY_DATE_KEYS, -1)
//
//        // set the value of terminal
//        etTerminalId.setText(terminalId)
//
//        if (terminalDate != -1L) {
//            val date = Date(terminalDate)
//            val dateStr = DateUtils.timeOfDateFormat.format(date)
//            tvTerminalInfoDate.text = getString(R.string.isw_title_date, dateStr)
//            tvTerminalInfo.text = getString(R.string.isw_title_terminal_config_downloaded)
//        } else {
//            val message = "No terminal configuration"
//            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
//            tvTerminalInfo.text = getString(R.string.isw_title_download_terminal_configuration)
//        }
//
//        if (keysDate != -1L) {
//            val date = Date(terminalDate)
//            val dateStr = DateUtils.timeOfDateFormat.format(date)
//            tvKeyDate.text = getString(R.string.isw_title_date, dateStr)
//            tvKeys.text = getString(R.string.isw_title_keys_downloaded)
//        } else {
//            val message = "No keys"
//            tvKeyDate.text = getString(R.string.isw_title_date, message)
//            tvKeys.text = getString(R.string.isw_title_download_keys)
//        }
//    }
//
//
//    private fun keysDownloaded(isSuccessful: Boolean) {
//        // enable and show button
//        btnDownloadKeys.isEnabled = true
//        btnDownloadKeys.visibility = View.VISIBLE
//        // hide progress bar
//        progressKeyDownload.visibility = View.GONE
//        // show download date
//        tvKeyDate.visibility = View.VISIBLE
//
//
//        if (isSuccessful) {
//            // get and store date
//            val date = Date()
//            store.saveNumber(SettingsActivity.KEY_DATE_KEYS, date.time)
//
//            // set the texts
//            val dateStr = DisplayUtils.getIsoString(date)
//            tvKeyDate.text = getString(R.string.isw_title_date, dateStr)
//            tvKeys.text = getString(R.string.isw_title_keys_downloaded)
//
//            // set the drawable and color
//            btnDownloadKeys.setImageResource(R.drawable.isw_ic_check)
//            val color = ContextCompat.getColor(requireContext(), R.color.iswTextColorSuccessDark)
//            ImageViewCompat.setImageTintList(btnDownloadKeys, ColorStateList.valueOf(color))
//        } else {
//            val message = "No keys downloaded"
//            tvKeyDate.text = getString(R.string.isw_title_date, message)
//            tvKeys.text = getString(R.string.isw_title_error_downloading_keys)
//
//            // set the drawable and color
//            btnDownloadKeys.setImageResource(R.drawable.isw_ic_error)
//            val color = ContextCompat.getColor(requireContext(), R.color.iswTextColorError)
//            ImageViewCompat.setImageTintList(btnDownloadKeys, ColorStateList.valueOf(color))
//        }
//
//    }
//
//    private fun terminalConfigDownloaded(isSuccessful: Boolean) {
//        // enable and show button
//        btnDownloadTerminalConfig.isEnabled = true
//        btnDownloadTerminalConfig.visibility = View.VISIBLE
//        // hide progress bar
//        progressTerminalDownload.visibility = View.GONE
//        // show download date
//        tvTerminalInfoDate.visibility = View.VISIBLE
//
//
//        if (isSuccessful) {
//            // get and store date
//            val date = Date()
//            store.saveNumber(SettingsActivity.KEY_DATE_TERMINAL, date.time)
//
//            // set the texts
//            val dateStr = DisplayUtils.getIsoString(date)
//            tvTerminalInfoDate.text = getString(R.string.isw_title_date, dateStr)
//            tvTerminalInfo.text = getString(R.string.isw_title_terminal_config_downloaded)
//
//            // set the drawable and color
//            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_check)
//            val color = ContextCompat.getColor(requireContext(), R.color.iswTextColorSuccessDark)
//            ImageViewCompat.setImageTintList(btnDownloadTerminalConfig, ColorStateList.valueOf(color))
//        } else {
//            val message = "No terminal configuration"
//            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
//            tvTerminalInfo.text = getString(R.string.isw_title_error_downloading_terminal_config)
//
//            // set the drawable and color
//            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_error)
//            val color = ContextCompat.getColor(requireContext(), R.color.iswTextColorError)
//            ImageViewCompat.setImageTintList(btnDownloadTerminalConfig, ColorStateList.valueOf(color))
//        }
//    }
//
//
    companion object {
        const val TAG = "Download Keys Fragment"
    }
}