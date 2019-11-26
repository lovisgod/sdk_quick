package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.adapters.RegisteredFingerprintAdapter
import com.interswitchng.smartpos.modules.main.dialogs.CreateFingerprintDialog
import com.interswitchng.smartpos.modules.main.viewmodels.FingerprintViewModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.LinearLayoutManagerWrapper
import kotlinx.android.synthetic.main.isw_fragment_registered_fingerprint.*
import org.koin.android.viewmodel.ext.android.viewModel

class RegisteredFingerprintFragment : BaseFragment(TAG) {

    private val viewModel: FingerprintViewModel by viewModel()
    private val registeredFingerprintAdapter by lazy { RegisteredFingerprintAdapter(requireContext()) }

    override val layoutId: Int
        get() = R.layout.isw_fragment_registered_fingerprint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        viewModel.registeredFingerprint.observe(this, Observer {
            registeredFingerprintAdapter.registeredFingerprints = it
        })
//        viewModel.getFingerprints(requireContext(), "")
    }

    private fun setupViews() {
        registered_fingerprints.apply {
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            adapter = registeredFingerprintAdapter
        }
        add_new_finger.setOnClickListener {
            val dialog = CreateFingerprintDialog()
            dialog.show(childFragmentManager, CreateFingerprintDialog.TAG)
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.getFingerprints(requireContext(), "")
    }

    companion object {
        const val TAG = "Registered Fingerprint"
    }
}