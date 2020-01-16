package com.interswitchng.smartpos.modules.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.utilities.hideMe
import com.interswitchng.smartpos.shared.utilities.isHomePage
import com.interswitchng.smartpos.shared.utilities.showMe
import kotlinx.android.synthetic.main.isw_activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.isw_navigation_fragment) }

    private val store: KeyValueStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!isSetup()) {
//            val intent = Intent(this, SetupActivity::class.java)
//            startActivity(intent)
//        }
        setContentView(R.layout.isw_activity_main)
        isw_bottom_navigation_view.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.isHomePage()) isw_bottom_navigation_view.showMe()
            else isw_bottom_navigation_view.hideMe()
        }
    }
}
