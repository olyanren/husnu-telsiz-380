package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TransceiverRequestModel (@SerializedName("OnEk") val preNumber: String,
                                    @SerializedName("BandrolNo") val number: String,
                                    @SerializedName("SonEk") val suffixNumber: String)