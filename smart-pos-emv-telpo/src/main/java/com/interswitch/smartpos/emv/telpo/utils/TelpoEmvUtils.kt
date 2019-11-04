package com.interswitch.smartpos.emv.telpo.utils

import com.interswitch.smartpos.emv.telpo.models.toAPPListItem
import com.interswitchng.smartpos.shared.models.posconfig.EmvAIDs
import com.interswitchng.smartpos.shared.models.posconfig.EmvCard
import com.interswitchng.smartpos.shared.models.posconfig.TerminalConfig
import com.telpo.emv.EmvApp

internal object TelpoEmvUtils {

    fun createAppList(terminalConfig: TerminalConfig, aiDs: EmvAIDs): List<EmvApp> {
        val creator = { card: EmvCard -> card.toAPPListItem(terminalConfig) }
        return aiDs.cards.map(creator).toList()
    }
}