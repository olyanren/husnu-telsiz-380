package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TaskCompleteRequestModel (
        @SerializedName("task_id") val taskId : Int,
        @SerializedName("location_id") val locationId : String
)