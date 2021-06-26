package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TransceiverResponseModel (@SerializedName("BandrolTuru") val barcodeTuru: String?,
                                     @SerializedName("Cevirmen") val cevirmen: String?,
                                     @SerializedName("EserAdi") val eserAdi: String?,
                                     @SerializedName("EserSahibi") val eserSahibi: String?,
                                     @SerializedName("GecerliMi") val gecerliMi: Boolean,
                                     @SerializedName("HataMesaji") val hataMesaji: String?,
                                     @SerializedName("Yayinevi") val yayinevi: String?)