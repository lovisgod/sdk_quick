package com.interswitchng.smartpos.di

import com.interswitchng.smartpos.modules.ussdqr.viewModels.QrViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.UssdViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

private val mainActivity = module {

}

private val ussdActivity = module {
    viewModel { UssdViewModel(get()) }
}

private val qrActivity = module {
    viewModel { QrViewModel(get()) }
}

val activityModules = listOf(mainActivity, ussdActivity, qrActivity)