package com.interswitchng.smartpos.shared.utilities

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager


/**
 * Created by ayokunlepaul on 20/03/2019.
 */
class LinearLayoutManagerWrapper constructor(context: Context) : LinearLayoutManager(context) {

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}