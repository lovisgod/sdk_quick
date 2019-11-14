package com.interswitchng.smartpos.modules.callhome


//
//internal class CallHomeViewModel(private val kimonoService: IsoService): RootViewModel()
//{
//
//
//    private val _callHomeResponse = MutableLiveData<Optional<String>>()
//    val callHomeResponse: LiveData<Optional<String>> get() = _callHomeResponse
//    fun callHome(request: CallHomeRequest, context: Context) {
//        // retrieve the qr code
//        uiScope.launch {
//
//            val result = withContext(ioScope) {
//                val response = kimonoService.callHome(request)
//
//                return@withContext when (response) {
//                    null -> None
//                    else -> {
//
//
//                        // extract result
//                        Some(response)
//                    }
//                }
//            }
//
//
//            _callHomeResponse.value = result.component1()
//        }
//    }
//}