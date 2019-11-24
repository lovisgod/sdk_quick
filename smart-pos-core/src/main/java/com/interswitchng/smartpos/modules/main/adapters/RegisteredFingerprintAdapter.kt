package com.interswitchng.smartpos.modules.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.RegisteredFingerprint
import com.interswitchng.smartpos.shared.utilities.AutoUpdateRecyclerView
import kotlin.properties.Delegates

class RegisteredFingerprintAdapter constructor(
    context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AutoUpdateRecyclerView {

    var registeredFingerprints: List<RegisteredFingerprint> by Delegates.observable(emptyList()) { _, oldFingerprints, newFingerprints ->
        autoNotify(oldFingerprints, newFingerprints) { oldFingerprint, newFingerprint ->
            oldFingerprint.userId == newFingerprint.userId && oldFingerprint.userName == newFingerprint.userName
        }
    }

    init { setHasStableIds(true) }

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == EMPTY_FINGERPRINT) {
            val rootView = inflater.inflate(R.layout.isw_layout_empty_fingerprints, parent, false)
            return EmptyFingerprintViewHolder(rootView)
        } else {
            val rootView = inflater.inflate(R.layout.isw_layout_registered_fingerprint, parent, false)
            RegisteredFingerprintViewHolder(rootView)
        }
    }

    override fun getItemId(position: Int): Long {
        return if (registeredFingerprints.isEmpty()) 0
        else registeredFingerprints[position].hashCode().toLong()
    }

    override fun getItemCount(): Int = if (registeredFingerprints.isEmpty()) 1
    else registeredFingerprints.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RegisteredFingerprintViewHolder) {
            holder.setFingerprint(registeredFingerprints[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (registeredFingerprints.isEmpty()) EMPTY_FINGERPRINT
        else REGISTERED_FINGERPRINT
    }

    inner class EmptyFingerprintViewHolder (view: View): RecyclerView.ViewHolder(view)

    inner class RegisteredFingerprintViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val userName = view.findViewById<TextView>(R.id.isw_user_name)
        private val userId = view.findViewById<TextView>(R.id.isw_user_id)

        fun setFingerprint(fingerprint: RegisteredFingerprint) {
            userName.text = fingerprint.userName
            userId.text = fingerprint.userId
        }
    }

    companion object {
        const val EMPTY_FINGERPRINT = 0
        const val REGISTERED_FINGERPRINT = 1
    }
}