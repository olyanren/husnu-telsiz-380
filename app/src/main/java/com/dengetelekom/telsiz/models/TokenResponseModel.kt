package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TokenResponseModel (@SerializedName("token_type") val token_type : String,
                               @SerializedName("expires_in") val expires_in : Int,
                               @SerializedName("access_token") val access_token : String,
                               @SerializedName("refresh_token") val refresh_token : String)