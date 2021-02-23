package com.interswitchng.smartpos.modules.main.transfer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R

class TransferActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(R.id.isw_navigation_fragment) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_transfer)
    }
}