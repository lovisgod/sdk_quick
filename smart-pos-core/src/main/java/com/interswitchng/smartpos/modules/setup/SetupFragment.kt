package com.interswitchng.smartpos.modules.setup

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_setup.*
import org.koin.android.viewmodel.ext.android.viewModel

class SetupFragment : BaseFragment(TAG) {

    private val setupViewModel: SetupFragmentViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.isw_fragment_setup

    companion object {
        const val TAG = "Setup Fragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel.fingerPrintBitmap.observe(this, Observer {
            if (it != null) {
                isw_merchant_finger.setImageBitmap(it)
                isw_remove_finger.visibility = View.VISIBLE
                isw_enroll_finger.visibility = View.GONE
            }
            else {
                isw_merchant_finger.setImageBitmap(null)
                isw_remove_finger.visibility = View.GONE
                isw_enroll_finger.visibility = View.VISIBLE
                isw_merchant_finger.setImageResource(R.drawable.ic_fingerprint_not_detected)
            }
        })
        setupViews()
    }

    private fun setupViews() {
        isw_sdk_version.text = BuildConfig.VERSION_NAME
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Kimono", "NIBBS"))
        isw_server_type.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                logger.logErr("Item position clicked === $position")
            }
        }

        isw_enroll_finger.setOnClickListener {
            setupViewModel.enrollMerchantFingerPrint()
        }

        isw_remove_finger.setOnClickListener {
            setupViewModel.removeMerchantFingerPrint()
        }
    }
}