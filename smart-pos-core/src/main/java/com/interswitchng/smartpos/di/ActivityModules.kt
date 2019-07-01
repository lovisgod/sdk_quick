package com.interswitchng.smartpos.di

import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.history.HistoryViewModel
import com.interswitchng.smartpos.modules.paycode.PayCodeViewModel
import com.interswitchng.smartpos.modules.report.ReportViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.QrViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.UssdViewModel
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


private val viewModels = module {

    viewModel { UssdViewModel(get()) }

    viewModel { QrViewModel(get()) }

    viewModel { PayCodeViewModel(get(), get()) }

    viewModel { CardViewModel(get(), get()) }

    viewModel { TransactionResultViewModel(get(), get(), get()) }

    viewModel { HistoryViewModel(get()) }

    viewModel { ReportViewModel(get()) }
}


val activityModules = listOf(viewModels)