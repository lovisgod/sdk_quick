package com.interswitchng.smartpos.modules.setup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.interswitchng.smartpos.R
import org.koin.android.viewmodel.ext.android.viewModel

class SetupActivity : AppCompatActivity() {

    private val setupViewModel: SetupFragmentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*  if (setupViewModel.isSetup()) {
              val intent = Intent(this, MainActivity::class.java)
              startActivity(intent)
          }*/
        setContentView(R.layout.isw_fragment_setup)
    }
}

