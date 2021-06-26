package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TokenRequestModel (

        @SerializedName("client_id") val clientId : Int,
        @SerializedName("client_secret") val clientSecret : String,
        @SerializedName("grant_type") val grantType : String,
        @SerializedName("username") val username : String,
        @SerializedName("password") val password : String,
)