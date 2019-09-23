package com.interswitchng.smartpos.modules.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.databinding.IswActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: IswActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.isw_activity_main)
        binding.iswBottomnavigationview.setupWithNavController(findNavController(R.id.isw_fragment))
    }
}