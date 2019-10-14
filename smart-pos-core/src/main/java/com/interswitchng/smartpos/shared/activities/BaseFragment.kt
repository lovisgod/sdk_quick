package com.interswitchng.smartpos.shared.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.fragments.CardPaymentFragment
import com.interswitchng.smartpos.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.UssdActivity
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.utilities.toast
import com.tapadoo.alerter.Alerter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

abstract class BaseFragment (fragmentName: String) : Fragment() {

    data class PollingText(val title: String, val subTitle: String)
    abstract val layoutId: Int

    protected lateinit var rootView: View
    protected val logger by lazy { Logger.with(fragmentName) }

    protected val iswPos: IswPos by inject()
    protected val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }
    private lateinit var pollingText: PollingText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(layoutId, container, false)
        pollingText = getPollingText()
        return rootView
    }

    protected fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    protected fun navigateUp() = findNavController().navigateUp()

    protected fun navigate(directionId: Int) {
        findNavController().navigate(directionId)
    }


    protected fun runWithInternet(handler: () -> Unit) {
        // ensure that device is connected to internet
        if (!DeviceUtils.isConnectedToInternet(context!!)) {
            context?.toast("Device is not connected to internet")
            // show no-network dialog
            DialogUtils.getNetworkDialog(context!!) {
                // trigger handler
                handler()
            }.show()
        } else {
            // trigger handler
            handler()
        }
    }

    private fun getPollingText(): PollingText {
        val isCodeActivity = this is  CardPaymentFragment|| this is CardPaymentFragment

        val title =
            if (isCodeActivity) getString(R.string.isw_title_confirmation_in_progress)
            else getString(R.string.isw_title_transaction_in_progress)

        val subTitle =
            if (isCodeActivity) getString(R.string.isw_sub_title_checking_transaction_status)
            else getString(R.string.isw_sub_title_processing_progress)

        return PollingText(title, subTitle)
    }

    protected fun showProgressAlert(canCancel: Boolean = true, oncancel: () -> Unit = {}) {
        Alerter.create(this.activity)
            .setTitle(pollingText.title)
            .setText(pollingText.subTitle)
            .enableProgress(true)
            .setDismissable(false)
            .enableInfiniteDuration(true)
            .setBackgroundColorRes(R.color.iswColorPrimaryDark)
            .setProgressColorRes(android.R.color.white).also {
                // add cancel  button if cancel is allowed
                if (canCancel) it.addButton("Cancel", R.style.AlertButton, View.OnClickListener {
                    Alerter.clearCurrent(this.activity)
                    context?.toast("Status check stopped")
                    oncancel()
                })
            }
            .show()
    }
}