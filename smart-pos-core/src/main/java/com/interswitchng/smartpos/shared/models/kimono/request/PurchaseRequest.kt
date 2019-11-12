package com.interswitchng.smartpos.shared.models.kimono.request

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root


data class CardData(@Element val  track2: Track2?, @Element val  emvData: EmvData?, @Element val  cardSequenceNumber: String?)

data class EmvData(@Element val  Cryptogram: String?, @Element val  TransactionType: String?, @Element val  AmountOther: String?, @Element val  AmountAuthorized: String?, @Element val  UnpredictableNumber: String?, @Element val  ApplicationInterchangeProfile: String?, @Element val  CryptogramInformationData: String?, @Element val  iad: String?, @Element val  TransactionCurrencyCode: String?, @Element val  atc: String?, @Element val  TerminalType: String?, @Element val  TerminalVerificationResult: String?, @Element val  CvmResults: String?, @Element val  TerminalCapabilities: String?, @Element val  DedicatedFileName: String?, @Element val  TerminalCountryCode: String?, @Element val  TransactionDate: String?)

data class PinData(@Element val  pinType: String?, @Element val  ksnd: String?)

@Root(name = "purchaseRequest")
data class PurchaseRequest(@Element val cardData: CardData?, @Element val  minorAmount: String?, @Element val  terminalInformation: TerminalInformation?, @Element val  fromAccount: String?, @Element val  stan: String?, @Element val  pinData: PinData?, @Element val  keyLabel: String?)

data class TerminalInformation(@Element val  merhcantLocation: String?, @Element val  posConditionCode: String?, @Element val  languageInfo: String?, @Element val  terminalId: String?, @Element val  posGeoCode: String?, @Element val  transmissionDate: String?, @Element val  terminalType: String?, @Element val  printerStatus: String?, @Element val  posDataCode: String?, @Element val  batteryInformation: String?, @Element val  merchantId: String?, @Element val  posEntryMode: String?, @Element val  currencyCode: String?, @Element val  uniqueId: String?)

data class Track2(@Element val  track2: String?, @Element val  expiryMonth: String?, @Element val  expiryYear: String?, @Element val  pan: String?)