package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TodoTitleModel(	@SerializedName("id") val id : Int,
                              @SerializedName("title") val title : String,
                              @SerializedName("is_checked") val isChecked : Boolean,
                              @SerializedName("created_at") val created_at : String,
                              @SerializedName("updated_at") val updated_at : String,
                              @SerializedName("deleted_at") val deleted_at : String) : BaseModel
