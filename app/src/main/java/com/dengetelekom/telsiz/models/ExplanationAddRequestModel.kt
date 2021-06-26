package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ExplanationAddRequestModel(

        @SerializedName("task_id") val taskId: Int,
        @SerializedName("location_id") val locationId: String,
        @SerializedName("explanation_id") val explanationId: Int,

        @SerializedName("explanation") val explanation: String,

        )