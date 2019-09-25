package com.interswitchng.smartpos.shared.utilities

import androidx.navigation.NavDestination
import com.interswitchng.smartpos.R

fun NavDestination.isHomePage(): Boolean = id == R.id.isw_transaction || id == R.id.isw_report ||
        id == R.id.isw_activity || id == R.id.isw_settings