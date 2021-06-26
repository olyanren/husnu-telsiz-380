package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ApiResponseModel (

        @SerializedName("success") val success : Boolean,
        @SerializedName("message") val message : String,


)