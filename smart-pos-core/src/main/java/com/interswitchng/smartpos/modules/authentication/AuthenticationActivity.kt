package com.interswitchng.smartpos.modules.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.R
import kotlinx.android.synthetic.main.isw_activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class AuthenticationActivity : AppCompatActivity() {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_login)
//        isw_sdk_version.text = getString(R.string.isw_sdk_version, BuildConfig.VERSION_NAME)
    }
}