package com.interswitchng.smartpos.shared

import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.models.core.Environment
import com.interswitchng.smartpos.shared.utilities.KeysUtils
import com.interswitchng.smartpos.shared.utilities.Logger

object Constants {

    // URL END POINTS
    internal const val CODE_END_POINT = "till.json"
    internal const val TRANSACTION_STATUS_QR = "transactions/qr"
    internal const val TRANSACTION_STATUS_USSD = "transactions/ussd.json"
    internal const val BANKS_END_POINT = "till/short-codes/1"
    internal const val AUTH_END_POINT = "oauth/token"

//    internal const val KIMONO_END_POINT = "kmw/v2/kimonoservice"
internal const val KIMONO_END_POINT = "kmw/kimonoservice"
    // EMAIL
    internal const val EMAIL_END_POINT = "mail/send"
    internal const val EMAIL_TEMPLATE_ID = "d-c33c9a651cea40dd9b0ee4615593dcb4"

    internal const val KEY_PAYMENT_INFO = "payment_info_key"

    internal const val KEY_MASTER_KEY = "master_key"
    internal const val KEY_SESSION_KEY = "session_key"
    internal const val KEY_PIN_KEY = "pin_key"

    const val KEY_ADMIN_PIN = "terminal_admin_access_pin_key"
    internal const val TERMINAL_CONFIG_TYPE = "kimono_or_nibss"
    internal const val SETTINGS_TERMINAL_CONFIG_TYPE = "settings_kimono_or_nibss"
    // UTIL CONSTANTS



    const val EMPTY_STRING = ""

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

    private object Production {

        const val ISW_USSD_QR_BASE_URL = "https://api.interswitchng.com/paymentgateway/api/v1/"
        const val ISW_TOKEN_BASE_URL = "https://passport.interswitchng.com/passport/"
        const val ISW_IMAGE_BASE_URL = "https://mufasa.interswitchng.com/p/paymentgateway/"
        const val ISW_KIMONO_URL = "https://kimono.interswitchng.com/kmw/v2/kimonoservice"
        const val ISW_KIMONO_BASE_URL = "https://kimono.interswitchng.com/"
        const val ISW_TERMINAL_IP = "196.6.103.73"
    }

    private object Test {
        const val ISW_USSD_QR_BASE_URL = "https://api.interswitchng.com/paymentgateway/api/v1/"
        const val ISW_TOKEN_BASE_URL = "https://passport.interswitchng.com/passport/"
        const val ISW_IMAGE_BASE_URL = "https://mufasa.interswitchng.com/p/paymentgateway/"
        const val ISW_KIMONO_URL = "https://qa.interswitchng.com/kmw/v2/kimonoservice"
        const val ISW_KIMONO_BASE_URL = "https://qa.interswitchng.com/"
        const val ISW_TERMINAL_IP = "196.6.103.72"
    }

}