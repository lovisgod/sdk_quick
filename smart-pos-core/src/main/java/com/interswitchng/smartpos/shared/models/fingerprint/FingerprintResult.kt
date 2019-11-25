package com.interswitchng.smartpos.shared.models.fingerprint

sealed class FingerprintResult {
    object Success : FingerprintResult()
    object Timeout: FingerprintResult()
    object ReadSuccessful: FingerprintResult()
    object ReadFailed: FingerprintResult()
}