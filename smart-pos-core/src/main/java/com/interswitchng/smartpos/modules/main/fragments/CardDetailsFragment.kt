package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.dialogs.AccountTypeDialog
import com.interswitchng.smartpos.modules.main.models.cardModel
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import kotlinx.android.synthetic.main.isw_fragment_card_details.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CardDetailsFragment : BaseFragment(TAG) {

    private val cardDetailsFragmentArgs by navArgs<CardDetailsFragmentArgs>()
    private val paymentModel by lazy { cardDetailsFragmentArgs.PaymentModel }

    private lateinit var accountTypeDialog: AccountTypeDialog
    private var accountType = AccountType.Default

    private val cardViewModel: CardViewModel by sharedViewModel()
    private lateinit var paymentInfo: PaymentInfo

    private val DEFAULT_AMOUNT = "0.00"

    private var amount = Constants.EMPTY_STRING

    override val layoutId: Int
        get() = R.layout.isw_fragment_card_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_card_details_toolbar.setNavigationOnClickListener { navigateUp() }
        val cardModel = cardModel {
            cvv = isw_cvv.text.toString()
            cardPan = isw_card_pan.text.toString()
            expiryDate = isw_card_expiry_date.text.toString()
        }
        paymentModel.newPayment {
            card = cardModel
        }
        isw_proceed.setOnClickListener {
            accountTypeDialog = AccountTypeDialog {
                accountType = when (it) {
                    0 -> AccountType.Default
                    1 -> AccountType.Savings
                    2 -> AccountType.Current
                    else -> AccountType.Default
                }
                paymentModel.newPayment {
                    amount = isw_amount.text.toString().toInt()
                }

//                runWithInternet {
//                    cardViewModel.processOnline(
//                        paymentInfo,
//                        terminalInfo
//                    )
//                }
            }
            paymentInfo = PaymentInfo(paymentModel.amount, IswPos.getNextStan())
            accountTypeDialog.show(childFragmentManager, CardTransactionsFragment.TAG)
            val direction =
                CardDetailsFragmentDirections.iswActionIswFragmentCardDetailsToIswProcessingTransaction(
                    paymentModel,
                    TransactionType.CardNotPresent,
                    CardType.None,
                    AccountType.Default,
                    paymentInfo
                )
            navigate(direction)
        }
    }

    companion object {
        const val TAG = "Card Details Fragment"
    }
}