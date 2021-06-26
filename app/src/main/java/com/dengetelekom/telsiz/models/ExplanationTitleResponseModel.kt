package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ExplanationTitleResponseModel(
        @SerializedName("success") val success : Boolean,
        @SerializedName("data") val data : List<ExplanationTitleModel>,
        @SerializedName("message") val message : String
)
