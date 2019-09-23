package com.interswitchng.smartpos.modules.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.databinding.IswActivityLoginBinding
import org.koin.android.viewmodel.ext.android.viewModel

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: IswActivityLoginBinding
    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.isw_activity_login)
        binding.version = BuildConfig.VERSION_NAME
        binding.iswTextview.text = HtmlCompat.fromHtml(getString(R.string.isw_smartpos), HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}