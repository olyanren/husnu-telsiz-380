package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class UrgentNotificationAddRequestModel(
        @SerializedName("image") val image: String,
        @SerializedName("explanation") val explanation: String, )