# SmartPOS SDK Technical Documentation
The SDK is divided into three(3) modules namely -
  - smart-pos-core
  - smart-pos-emv-pax
  - smart-pos-usb
  

To start using the SDK, follow the steps below:
- add module `smart-pos-core` and module `smart-pos-emv-pax` to your project. If you want to make use of USB communication between the SDK and a PC, add the third module `smart-pos-usb`
- Configure the SDK in your android `Application` class or your main `Activity` like below
```Java
// create POSDeviceService instance for pax device
POSDeviceService deviceService = 
POSDeviceService.create(getApplicationContext());

String merchantCode = "MX5882";
// initialize POSConfig with your merchant Code
POSConfig config = new POSConfig(merchantCode);

// configure the SDK using the configureTerminal method
IswPos.configureTerminal(getApplication(), deviceService, config);
```

In order to communicate effectively with the terminal, some initialization and configuration needs to be done, the above code does these.

### Initiating Payment

You can trigger SDK payment flow using the code below

```Java
int KOBO = 100;
int amount = 1000 * KOBO; // convert to kobo

// to start the payment flow, pass the following parameters to
// IswPos#initiatePayment()
// activity - the calling activity instance
// amount - the amount you want to accept
// paymentType - used to trigger a specific payment type
// there are four(4) payment types that you can use
// and they are (CARD, USSD, QR and PAYCODE), if paymentType is null
// CARD paymentType will be used be default
IswPos.getIswPos().initiatePayment(activity, amount, paymentType);
```

The SDK will take over from here. You can override your Activity's `onActivityResult` method to handle the result of the initiated payment

```Java
    @Override
    protected void onActivityResult(int requestCode,
    int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // handle success
            PurchaseResult result = IswPos.getResult(data);
            // process result here
            // you can access the following properties in PurchaseResult
            // class PurchaseResult {
            //     String responseCode = "";
            //     String responseMessage = "";
            //     String transactionReference = "";
            // }
        } else {
            // else handle error
        }
    }
```

##### Note on USB communication

For USB communication between the SDK and a PC to work, the below steps need to be followed

- ADB must be installed and added to Path enviroment variable on the PC
- USB Debugging must be enabled in the Android terminal