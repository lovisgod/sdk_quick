package com.interswitchng.smartpos.modules.menu.settings

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.MainActivity
import com.interswitchng.smartpos.modules.main.dialogs.FingerprintBottomDialog
import com.interswitchng.smartpos.modules.main.dialogs.MerchantCardDialog
import com.interswitchng.smartpos.modules.setup.SetupActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.MenuActivity
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import com.interswitchng.smartpos.shared.services.kimono.models.TerminalInformation
import com.interswitchng.smartpos.shared.utilities.*
import kotlinx.android.synthetic.main.isw_activity_terminal_settings.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class TerminalSettingsActivity : MenuActivity() {

    private val instance: IswPos by inject()
    private val store: KeyValueStore by inject()
    private val settingsViewModel: SettingsViewModel by viewModel()

    private val isFromSettings by lazy { intent.getBooleanExtra("FROM_SETTINGS", false) }

    private var supervisorCardIsEnrolled = false

    private val alert by lazy {
        DialogUtils.getAlertDialog(this)
                .setTitle("Invalid Configuration")
                .setMessage("The configuration contains invalid parameters, please fix the errors and try saving again")
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_terminal_settings)

        setSupportActionBar(toolbar)
        if (isFromSettings) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            if (TerminalInfo.get(store) != null) {
                //That is when the enrollment should take place
//                val intent = Intent(this, SetupActivity::class.java)
//                startActivity(intent)
//                finish()
            } else {
                //That is when the enrollment should take place
            }


        }
        fetchSupervisorDetails()
        // setup button listeners
        setupButtons()
        // set the text values
        setupTexts()

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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.isw_menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // set back click to go back
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        } else if (item?.itemId == R.id.saveConfig) {
            saveConfig()
            if (!isFromSettings) {
//                val intent = Intent(this, SetupActivity::class.java)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupButtons() {
        // function validate nibbs request config
        val isValidNibbsRequest: (String, String, String) -> Boolean = { terminalId, ip, port ->

            // check validity of all fields
            val invalidTerminalId = InputValidator(terminalId)
                    .isNotEmpty().isAlphaNumeric()
                    .isExactLength(8)

            val invalidServerIp = InputValidator(ip).isNotEmpty().isValidIp()

            val invalidServerPort = InputValidator(port).isNotEmpty()
                    .isNumber().hasMaxLength(5)
                    .isNumberBetween(0, 65535)


            // show all error on the page
            if (invalidTerminalId.hasError) etTerminalId.error = invalidTerminalId.message
            if (invalidServerIp.hasError) etServerIP.error = invalidServerIp.message
            if (invalidServerPort.hasError) etServerPort.error = invalidServerPort.message


            // ensure no parameter is invalid
            !(invalidTerminalId.hasError
                    && invalidServerIp.hasError
                    && invalidServerPort.hasError)
        }

        // set up buttons
        btnDownloadKeys.setOnClickListener {

            // get fields
            val terminalID: String = etTerminalId.text.toString()
            val serverIp: String = etServerIP.text.toString()
            val serverPort: String = etServerPort.text.toString()

            // check validity
            val isValid = isValidNibbsRequest(terminalID, serverIp, serverPort)

            if (isValid) {
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
                settingsViewModel.downloadKeys(
                        terminalID,
                        serverIp,
                        serverPort.toInt(),
                        switchKimono.isChecked,
                        switchToNIBBS.isChecked
                )
            }
        }

        btnDownloadTerminalConfig.setOnClickListener {

            // get fields
            val terminalID: String = etTerminalId.text.toString()
            val serverIp: String = etServerIP.text.toString()
            val serverPort: String = etServerPort.text.toString()

            // check validity
            val isValid = isValidNibbsRequest(terminalID, serverIp, serverPort)

            // validate terminal id
            if (isValid) {
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
                settingsViewModel.downloadTerminalConfig(
                        terminalID,
                        serverIp,
                        serverPort.toInt(),
                        switchKimono.isChecked
                )
            }
        }

        btnUploadConfig.setOnClickListener {
            // create intent to choose file
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "text/xml"
            }

            // choose config file
            startActivityForResult(intent, RC_FILE_READ)
        }
        /*  switchToNIBBS.setOnClickListener { button, _ ->
              if(button.isChecked){

              }
          }*/

        switchKimono.setOnCheckedChangeListener { button, _ ->

            // hide error messages for non required fields
            if (button.isChecked) {
                // ip and port not required if kimono
                etServerPort.error = null
                etServerIP.error = null

                // hide server and port fields
                etServerPort.isEnabled = false
                etServerIP.isEnabled = false

                etAgentId.isEnabled = true
                etAgentEmail.isEnabled = true

                // show server url field
                etServerUrl.isEnabled = true

                configSettings.text = "Configured To Kimono"
            } else {
                // server not required if not kimono
                etServerUrl.error = null
                // hide server url field
                etServerUrl.isEnabled = false

                //agentId not required if not kimono
                etAgentId.error = null
                //agentEmail not required if not kimono
                etAgentEmail.error = null

                etAgentId.isEnabled = false
                etAgentEmail.isEnabled = false

                // show server and port fields
                etServerPort.isEnabled = true
                etServerIP.isEnabled = true

                configSettings.text = "Configured to NIBBS"
            }

            // set the terminal-info download container based on kimono flag
            terminalInfoDownloadContainer.visibility =
                    if (button.isChecked) View.GONE else View.VISIBLE

            // set the agentId container based on kimono flag
            agentId.visibility =
                    if (button.isChecked) View.VISIBLE else View.GONE

            // set the agentEmail container based on kimono flag
            agentEmail.visibility =
                    if (button.isChecked) View.VISIBLE else View.GONE

        }


        btnChangePassword.setOnClickListener {
            if (supervisorCardIsEnrolled) {
                authorizeAndPerformAction { fetchSupervisorDetails() }
            } else {
                val intent = Intent(this, SetupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupTexts(terminalInfo: TerminalInfo? = TerminalInfo.get(store)) {
        val terminalDate = store.getNumber(KEY_DATE_TERMINAL, -1)
        val keysDate = store.getNumber(KEY_DATE_KEYS, -1)


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


        // set up field texts with keyTerminalInfo
        terminalInfo?.apply {
            // terminal config
            etTerminalId.setText(terminalId)
            etMerchantId.setText(merchantId)
            etMerchantCategoryCode.setText(merchantCategoryCode)
            etMerchantNameAndLocation.setText(merchantNameAndLocation)
            etCountryCode.setText(countryCode)
            etCurrencyCode.setText(currencyCode)
            etCallHomeTime.setText(callHomeTimeInMin.toString())
            etServerTimeout.setText(serverTimeoutInSec.toString())
            etCapabilities.setText(capabilities)
            etAgentId.setText(agentId)
            etAgentEmail.setText(agentEmail)

            switchKimono.isChecked = isKimono
        }
        val serverIp = terminalInfo?.serverIp ?: Constants.ISW_TERMINAL_IP
        val serverPort = terminalInfo?.serverPort ?: BuildConfig.ISW_TERMINAL_PORT
        val serverUrl = terminalInfo?.serverUrl ?: Constants.ISW_KIMONO_URL


        // server config
        etServerIP.setText(serverIp)
        etServerPort.setText(serverPort.toString())
        etServerUrl.setText(serverUrl)

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

            // save config if its kimono
            if (switchKimono.isChecked) saveConfig()
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

            // setup text
            setupTexts()

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_check)
            val color = ContextCompat.getColor(this, R.color.iswTextColorSuccessDark)
            ImageViewCompat.setImageTintList(
                    btnDownloadTerminalConfig,
                    ColorStateList.valueOf(color)
            )
        } else {
            val message = "No terminal configuration"
            tvTerminalInfoDate.text = getString(R.string.isw_title_date, message)
            tvTerminalInfo.text = getString(R.string.isw_title_error_downloading_terminal_config)

            // set the drawable and color
            btnDownloadTerminalConfig.setImageResource(R.drawable.isw_ic_error)
            val color = ContextCompat.getColor(this, R.color.iswTextColorError)
            ImageViewCompat.setImageTintList(
                    btnDownloadTerminalConfig,
                    ColorStateList.valueOf(color)
            )
        }
    }

    // function to extract terminal info form input elements
    private fun getTerminalInfo(): TerminalInformation {
        return TerminalInformation().apply {
            terminalId = etTerminalId.getString()
            merchantId = etMerchantId.getString()
            merchantCategoryCode = etMerchantCategoryCode.getString()
            merchantNameAndLocation = etMerchantNameAndLocation.getString()
            countryCode = etCountryCode.getString()
            currencyCode = etCurrencyCode.getString()
            callHomeTimeInMin = etCallHomeTime.getString()
            serverTimeoutInSec = etServerTimeout.getString()
            serverIp = etServerIP.getString()
            serverPort = etServerPort.getString()
            serverUrl = etServerUrl.getString()
            isKimono = switchKimono.isChecked
            agentId = etAgentId.getString()
            agentEmail = etAgentEmail.getString()


            // only set capabilities if it was provided
            etCapabilities.getString().apply {
                capabilities = if (this.isNotEmpty()) this else null
            }
        }
    }

    private fun setTerminalInfoError(errorInfo: TerminalInformation) {
        errorInfo.apply {
            if (terminalId.isNotEmpty()) tiTerminalId.error = terminalId
            if (merchantId.isNotEmpty()) tiMerchantId.error = merchantId
            if (merchantCategoryCode.isNotEmpty()) tiMerchantCategoryCode.error =
                    merchantCategoryCode
            if (merchantNameAndLocation.isNotEmpty()) tiMerchantNameAndLocation.error =
                    merchantNameAndLocation
            if (countryCode.isNotEmpty()) tiCountryCode.error = countryCode
            if (currencyCode.isNotEmpty()) tiCurrencyCode.error = currencyCode
            if (callHomeTimeInMin.isNotEmpty()) tiCallHomeTime.error = callHomeTimeInMin
            if (serverTimeoutInSec.isNotEmpty()) tiServerTimeout.error = serverTimeoutInSec
            if (serverIp.isNotEmpty()) tiServerIP.error = serverIp
            if (capabilities?.isNotEmpty() == true) tiCapabilities.error = capabilities
            if (serverPort.isNotEmpty()) tiServerPort.error = serverPort
            if (serverUrl.isNotEmpty()) tiServerUrl.error = serverUrl
            if (agentId.isNotEmpty()) tiAgentId.error = agentId
            if (agentEmail.isNotEmpty()) tiAgentEmail.error = agentEmail
        }

        alert.show()
    }

    private fun saveConfig(terminalInfo: TerminalInformation = getTerminalInfo()) {
        // ensure extracted information is valid
        if (terminalInfo.isValid) {
            // check if config parameters changed
            val isNew = terminalInfo.toTerminalInfo.persist(store)

            // toast based on status
            if (isNew) toast("Config saved successfully!")
            else toast("Config has not changed!")
        } else {
            setTerminalInfoError(terminalInfo.error)
            toast("Error: Invalid configuration loaded into terminal")
        }
    }


    private fun validateUserPassword() {
        // get old password and check validation
        val oldPassword = etOldPassword.text.toString()
        val oldPasswordValidator = InputValidator(oldPassword)
                .isNotEmpty().isExactLength(6).isNumber()

        // get new password and check validation
        val newPassword = etNewPassword.text.toString()
        val newPasswordValidator = InputValidator(newPassword)
                .isNotEmpty().isExactLength(6).isNumber()

        // confirm password if no validation error
        if (!oldPasswordValidator.hasError) {
            // get admin configured pin
            var existingPin = store.getString(Constants.KEY_ADMIN_PIN, "")
            if (existingPin.isEmpty()) existingPin = BuildConfig.ISW_DEFAULT_PIN

            // hash provided pin
            val providedPin = SecurityUtils.getSecurePassword(oldPassword)
            val isValidPin = providedPin == existingPin

            if (!isValidPin) {
                oldPasswordValidator.hasError = true
                oldPasswordValidator.message = "invalid password"
            } else if (isValidPin && !newPasswordValidator.hasError) {
                // if new and old passwords are valid
                // hash the new password
                val newPasswordHash = SecurityUtils.getSecurePassword(newPassword)
                // save the new password
                store.saveString(Constants.KEY_ADMIN_PIN, newPasswordHash)
                toast("New Password Saved")
            }
        }

        // show any errors
        if (oldPasswordValidator.hasError) tiOldPassword.error = oldPasswordValidator.message
        if (newPasswordValidator.hasError) tiNewPassword.error = newPasswordValidator.message
    }


    private lateinit var dialog: MerchantCardDialog


    private fun performOperation() {

    }

    private fun authorizeAndPerformAction(action: () -> Unit) {
        val fingerprintDialog = FingerprintBottomDialog(isAuthorization = true) { isValidated ->
            if (isValidated) {
                action.invoke()
            } else {
                toast("Unauthorized Access!!")
            }
        }

        dialog = MerchantCardDialog {
            when (it) {
                MerchantCardDialog.AUTHORIZED -> action.invoke()
                MerchantCardDialog.FAILED -> toast("Unauthorized Access!!")
                MerchantCardDialog.USE_FINGERPRINT -> fingerprintDialog.show(
                        supportFragmentManager,
                        FingerprintBottomDialog.TAG
                )
            }
        }
        dialog.show(supportFragmentManager, MerchantCardDialog.TAG)
    }


    private fun fetchSupervisorDetails() {
        val savedPan = store.getString("M3RCHANT_PAN", "")
        if (savedPan == "") {
            supervisorStatusHeader.text = "Supervisor's card not set"
            btnChangePassword.text = "Enroll supervisor's card"
            supervisorCardIsEnrolled = false
        } else {
            supervisorStatusHeader.text = ""
            btnChangePassword.text = "Change supervisor's card"
            supervisorCardIsEnrolled = true
        }
    }


    private val cardViewModel: CardViewModel by viewModel()

    val terminalInfo by lazy { TerminalInfo.get(store)!! }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == RC_FILE_READ) {

            data?.data?.also { uri ->
                contentResolver.openInputStream(uri)?.use {
                    // extract and save terminal info
                    val terminalInfo = settingsViewModel.getTerminalInformation(it)
                    toast("Config file loaded successfully")
                    // set text for loaded information
                    setupTexts(terminalInfo.toTerminalInfo)
                    // save current config
                    saveConfig(terminalInfo)
                }
            }

        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun EditText.getString(): String {
        return this.text.toString()
    }

    companion object {
        const val KEY_DATE_TERMINAL = "key_download_terminal_date"
        const val KEY_DATE_KEYS = "key_download_key_date"
        const val RC_FILE_READ = 49239


        const val AUTHORIZED = 0
        const val FAILED = 1
        const val USE_FINGERPRINT = 2
        const val TAG = "Merchant Card Dialog"
    }
}