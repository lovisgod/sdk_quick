package com.interswitchng.smartpos.di

import com.interswitchng.smartpos.modules.authentication.AuthenticationViewModel
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.billPayment.viewmodels.BillPaymentViewmodel
import com.interswitchng.smartpos.modules.main.transfer.TransferViewModel
import com.interswitchng.smartpos.modules.main.transfer.viewmodels.TransactionHistoryViewmodel
import com.interswitchng.smartpos.modules.main.viewmodels.FingerprintViewModel
import com.interswitchng.smartpos.modules.menu.history.HistoryViewModel
import com.interswitchng.smartpos.modules.menu.report.ReportViewModel
import com.interswitchng.smartpos.modules.menu.settings.SettingsViewModel
import com.interswitchng.smartpos.modules.paycode.PayCodeViewModel
import com.interswitchng.smartpos.modules.setup.SetupFragmentViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.QrViewModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.UssdViewModel
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module


internal val viewModels = module {

    /**Transfer viewmodel**/
    viewModel { TransferViewModel(get(), get()) }

    viewModel {
        BillPaymentViewmodel(get())
    }

    viewModel { UssdViewModel(get()) }

    viewModel { QrViewModel(get()) }

    /** TransactionHistory Viewmodel**/
    viewModel { TransactionHistoryViewmodel(get(), get()) }

    viewModel {
        val store: KeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        val isKimono = terminalInfo?.isKimono ?: false
        val isoService: IsoService =  get { parametersOf(isKimono) }
        PayCodeViewModel(isoService, get())
    }

    viewModel { SetupFragmentViewModel(get(), get()) }

    viewModel {
        val store: KeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        val isKimono = terminalInfo?.isKimono ?: false

        val isoService: IsoService = get { parametersOf(isKimono)}

        CardViewModel(get(), isoService)
    }

    viewModel { FingerprintViewModel(get(), get()) }

    viewModel {
        val store: KeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        val isKimono = terminalInfo?.isKimono ?: false

        val isoService: IsoService = get { parametersOf(isKimono)}
        TransactionResultViewModel(get(), get(), get(), isoService)
    }

    viewModel { HistoryViewModel(get()) }

    viewModel { ReportViewModel(get(), get(), get()) }

    viewModel {
        SettingsViewModel()
    }

    viewModel { AuthenticationViewModel() }
}