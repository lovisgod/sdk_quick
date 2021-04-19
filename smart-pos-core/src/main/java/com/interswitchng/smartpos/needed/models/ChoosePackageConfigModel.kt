
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChoosePackageConfigModel(
        var billerName: String,
        val billerCode: String? = "",
        val accountNumberField: FieldConfig? = FieldConfig(),
        val phoneNumberField: FieldConfig? = FieldConfig(title = "Phone Number", show = false, required = false),
        val selectPackageField: FieldConfig? = FieldConfig(title = "Select Package"),
        val amountField: FieldConfig? = FieldConfig(title = "Amount"),
        val billerCodeField: FieldConfig? = FieldConfig(title = "Biller Code")
): Parcelable {

    @Parcelize
    data class FieldConfig (
            var show: Boolean? = true,
            var title: String? = "Smart Card Number",
            val placehoder: String? = "",
            var required: Boolean = true
    ) : Parcelable

}
