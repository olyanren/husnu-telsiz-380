package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotificationModel(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: String,


) : BaseModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationModel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
