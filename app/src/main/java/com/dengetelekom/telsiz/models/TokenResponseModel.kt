package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TokenResponseModel (@SerializedName("token_type") val token_type : String,
                               @SerializedName("expires_in") val expires_in : Int,
                               @SerializedName("access_token") val access_token : String,
                               @SerializedName("refresh_token") val refresh_token : String,
                               @SerializedName("company_name") val companyName: String,
                               @SerializedName("is_check_in_available") val isCheckInAvailable: Boolean,
                               @SerializedName("is_notification_available") val isNotificationAvailable: Boolean,
                               @SerializedName("onesignal_app_id") val onesignalAppId: String?,
                               @SerializedName("user_id") val userId: Int?,
                               @SerializedName("nfc_reader_active") val nfcReaderActive : Boolean?,
                               @SerializedName("barcode_reader_active") val barcodeReaderActive : Boolean?)