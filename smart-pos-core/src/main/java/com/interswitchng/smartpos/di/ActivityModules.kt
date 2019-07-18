package com.interswitchng.smartpos.di

import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.menu.history.HistoryViewModel
import com.interswitchng.smartpos.modules.paycode.PayCodeViewModel
import com.interswitchng.smartpos.modules.menu.report.ReportViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.QrViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.UssdViewModel
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


internal val viewModels = module {

    viewModel { UssdViewModel(get()) }

    viewModel { QrViewModel(get()) }

    viewModel { PayCodeViewModel(get(), get()) }

    viewModel { CardViewModel(get(), get()) }

    viewModel { TransactionResultViewModel(get(), get(), get()) }

    viewModel { HistoryViewModel(get()) }

    viewModel { ReportViewModel(get()) }
}
