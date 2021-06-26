package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ToDoAddRequestModel (
        @SerializedName("task_id") val taskId : Int,
        @SerializedName("location_id") val locationId : String,
        @SerializedName("todo_ids") val todoIds : List<Int>,
)