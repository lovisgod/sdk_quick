package com.interswitchng.smartpos.shared.models.printer.info

sealed class PrintStatus(val message: String) {
    class Ok(message: String): PrintStatus(message)
    class Error(message: String): PrintStatus(message)
}