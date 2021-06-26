package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TodoTitleResponseModel(
        @SerializedName("success") val success : Boolean,
        @SerializedName("data") val data : List<TodoTitleModel>,
        @SerializedName("message") val message : String
)
