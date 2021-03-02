package com.interswitchng.smartpos.shared

import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.modules.main.transfer.models.BankModel
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.utilities.KeysUtils

object Constants {



    /**
     * payment code for surcharge**/
    internal const val SURHARGE_CODE_1 = "1075" //transfer between 1 and 5000
    internal const val SURHARGE_CODE_2 = "2688" //transfer between 5001 and 50,000
    internal const val SURHARGE_CODE_3 = "5375" //transfer between 50,001 and above

    /*internal const val PAYMENT_CODE_1 = "04385601"
    internal const val PAYMENT_CODE_2 = "04385602"*/

    internal const val PAYMENT_CODE_1 = "04386203" //fund wallet code for transaction amount between 1 and 3000
    internal const val PAYMENT_CODE_2 = "04386204" //payment notification code amount between 1 and 3000
    internal const val PAYMENT_CODE_3 = "04386205" //fund wallet between 3001 and 8000
    internal const val PAYMENT_CODE_4 = "04386206" //payment notification code between 3001 and 8000
    internal const val PAYMENT_CODE_5 = "04385603" //fund wallet greater than 8000
    internal const val PAYMENT_CODE_6 = "04385604" //payment notification code greater than 8000

    // URL END POINTS
    internal const val CODE_END_POINT = "till.json"
    internal const val TRANSACTION_STATUS_QR = "transactions/qr"
    internal const val TRANSACTION_STATUS_USSD = "transactions/ussd.json"
    internal const val BANKS_END_POINT = "till/short-codes/1"
    internal const val AUTH_END_POINT = "oauth/token"
    internal const val KIMONO_KEY_END_POINT = "/kmw/keydownloadservice"
    internal const val AGENT_ID = "https://pos-k8s-prod.bankly.ng/api/PosTerminal/GetAssignedUser/"


    //    internal const val KIMONO_END_POINT = "kmw/v2/kimonoservice"
    internal const val KIMONO_END_POINT = "kmw/kimonoservice"

    internal const val SATURN_END_POINT = "https://saturn.interswitchng.com/api/"


    internal const val KIMONO_CASH_OUT_ENDPOINT_INQUIRY = "https://webpay.interswitchng.com/kimonotms/paybills/inquire"

    internal const val KIMONO_CASH_OUT_ENDPOINT_PAY = "https://webpay.interswitchng.com/kimonotms/paybills/pay"

    internal const val KIMONO_3_END_POINT = "https://kimono.interswitchng.com/kmw/kimonoservice"

    internal const val KIMONO_TRANSFER_END_POINT = "https://kimono.interswitchng.com/kmw/kimonoservice/amex"

    internal const val KIMONO_TOKEN_END_POINT = "https://saturn.interswitchng.com:443/kimonotms/requesttoken/perform-process"


    // EMAIL
    internal const val EMAIL_END_POINT = "mail/send"
    internal const val EMAIL_TEMPLATE_ID = "d-c33c9a651cea40dd9b0ee4615593dcb4"

    internal const val KEY_PAYMENT_INFO = "payment_info_key"

    internal const val KEY_MASTER_KEY = "master_key"
    internal const val KEY_SESSION_KEY = "session_key"
    internal const val KEY_PIN_KEY = "pin_key"
    internal const val KIMONO_KEY = "kimono_key"

    const val KEY_ADMIN_PIN = "terminal_admin_access_pin_key"
    internal const val TERMINAL_CONFIG_TYPE = "kimono_or_nibss"
    internal const val SETTINGS_TERMINAL_CONFIG_TYPE = "settings_kimono_or_nibss"
    // UTIL CONSTANTS



    const val EMPTY_STRING = ""
    const val PKMOD = "qAujj7YjqEihDiSMbEeGyqnib5YTi3SCyB57l8gV5nPjHd6kVvLImVZbmqjyixGPuIK4l5IASfhQ50VwRpKQ9x7VWD7DIOvu1%2bpdDDnbuzyFfVEINT/RpYBxYh6MooEUl/WvTh2Ym2snJ1GtfXLtQpeT3HnB60kbLjdLfs0k2%2bE%3d"
    const val PKEX = "AAEAAQ%3d%3d"
//
//
//    internal  const val  KIMONO_CALL_HOME="kimonoservice"
//
//
//

    val ISW_USSD_QR_BASE_URL: String get() {
        val iswPos = IswPos.getInstance()
        return if (iswPos.config.environment == Environment.Test) Constants.Test.ISW_USSD_QR_BASE_URL
        else Constants.Production.ISW_USSD_QR_BASE_URL
    }

    val ISW_TOKEN_BASE_URL: String get() {
        val iswPos = IswPos.getInstance()
        return if (iswPos.config.environment == Environment.Test) Constants.Test.ISW_TOKEN_BASE_URL
        else Constants.Production.ISW_TOKEN_BASE_URL
    }

    val ISW_IMAGE_BASE_URL: String get() {
        val iswPos = IswPos.getInstance()
        return if (iswPos.config.environment == Environment.Test) Constants.Test.ISW_IMAGE_BASE_URL
        else Constants.Production.ISW_IMAGE_BASE_URL
    }

    val ISW_TERMINAL_IP: String get() {
        val iswPos = IswPos.getInstance()
        return if (iswPos.config.environment == Environment.Test) Constants.Test.ISW_TERMINAL_IP
        else Constants.Production.ISW_TERMINAL_IP
    }

    val ISW_DUKPT_IPEK: String get() {
        val iswPos = IswPos.getInstance()
        return if(iswPos.config.environment == Environment.Test) KeysUtils.testIPEK()
        else KeysUtils.productionIPEK()
    }


    val ISW_DUKPT_KSN: String get() {
        val iswPos = IswPos.getInstance()
        return if(iswPos.config.environment == Environment.Test) KeysUtils.testKSN()
        else KeysUtils.productionKSN()
    }

    val ISW_CMS: String get() {
        val iswPos = IswPos.getInstance()
        //return KeysUtils.testCMS()
        return if(iswPos.config.environment == Environment.Test) KeysUtils.testCMS()
        else KeysUtils.productionCMS()

    }

    val ISW_CMS_TEST: String
        get() {
            return KeysUtils.testCMS()
        }


    val ISW_KIMONO_BASE_URL: String get() {
        val iswPos = IswPos.getInstance()
        return if(iswPos.config.environment == Environment.Test) Test.ISW_KIMONO_BASE_URL
        else Production.ISW_KIMONO_BASE_URL
    }

    val ISW_KIMONO_URL: String get() {
        val iswPos = IswPos.getInstance()
        return if(iswPos.config.environment == Environment.Test) Test.ISW_KIMONO_URL
        else Production.ISW_KIMONO_URL
    }

    val ISW_KIMONO_KEY_URL: String
        get() {
            return Production.ISW_KEY_DOWNLOAD_URL
        }
    val ISW_TERMINAL_CONFIG_URL: String
        get() {
            return Production.ISW_TERMINAL_CONFIG_DOWNLOAD_URL
        }


    val ISW_EPMS_PORT = "5043"
    val ISW_CTMS_PORT = "5008"
    val ISW_TERMINAL_IP_EPMS = "196.6.103.73"
    val ISW_TERMINAL_IP_CTMS = "196.6.103.18"
    val ISW_TERMINAL_CAPABILITIES = "E0F0C8"
    val ISW_SERVER_TIMEOUT_IN_SEC = "60"
    val ISW_CALL_HOME_TIME_IN_MIN ="60"
    val ISW_COUNTRY_CODE = "566"
    val ISW_CURRENCY_CODE = "566"
    val ISW_MERCHANT_CODE = "MX1065"
    val ISW_MERCHANT_ALIAS = "000001"



    private object Production {

        const val ISW_USSD_QR_BASE_URL = "https://api.interswitchng.com/paymentgateway/api/v1/"
        const val ISW_TOKEN_BASE_URL = "https://passport.interswitchng.com/passport/"
        const val ISW_IMAGE_BASE_URL = "https://mufasa.interswitchng.com/p/paymentgateway/"
        const val ISW_KIMONO_URL = "https://kimono.interswitchng.com/kmw/v2/kimonoservice"
        const val ISW_KIMONO_BASE_URL = "https://webpay.interswitchng.com/"
        const val ISW_TERMINAL_IP = "196.6.103.73"
        const val ISW_KEY_DOWNLOAD_URL = "http://kimono.interswitchng.com/kmw/keydownloadservice"
        const val ISW_TERMINAL_CONFIG_DOWNLOAD_URL = "http://kimono.interswitchng.com/kmw/serialid/"
    }

    private object Test {
        const val ISW_USSD_QR_BASE_URL = "https://api.interswitchng.com/paymentgateway/api/v1/"
        const val ISW_TOKEN_BASE_URL = "https://passport.interswitchng.com/passport/"
        const val ISW_IMAGE_BASE_URL = "https://mufasa.interswitchng.com/p/paymentgateway/"
        const val ISW_KIMONO_URL = "https://qa.interswitchng.com/kmw/v2/kimonoservice"
        const val ISW_KIMONO_BASE_URL = "https://webpay.interswitchng.com/"
        const val ISW_TERMINAL_IP = "196.6.103.72"

    }

    val BANK_LIST = arrayListOf<BankModel>(
            BankModel("639563", "084", "Heritage"),
            BankModel("627955", "082", "Keys Bank"),
            BankModel("639609", "215", "Unity"),
            BankModel("639138", "070", "Fidelity Bank"),
            BankModel("628009", "214", "FCMB"),
            BankModel("627858", "039", "Stanbic IBTC"),
            BankModel("627168", "063", "Access (Diamond)"),
            BankModel("603948", "050", "Eco Bank"),
            BankModel("627821", "035", "Wema Bank"),
            BankModel("639249", "032", "Sterling Bank"),
            BankModel("589019", "011", "First Bank"),
            BankModel("627787", "058", "GTBank"),
            BankModel("636088", "044", "Access Bank"),
            BankModel("636092", "076", "Polaris"),
            BankModel("627480", "033", "UBA"),
            BankModel("627629", "057", "Zenith Bank")
    )

    const val SETTLEMENT_ACCOUNT_NUMBER = "SETTLEMENT_ACCOUNT_NUMBER"
    const val SETTLEMENT_BANK_CODE = "SETTLEMENT_BANK_CODE"
    const val SETTLEMENT_PIN = "090909"
    const val FOR_SETTLEMENT_ACCOUNT_SETUP = "FOR_SETTLEMENT_ACCOUNT_SETUP"

}