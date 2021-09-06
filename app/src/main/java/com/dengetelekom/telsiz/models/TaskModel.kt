package com.dengetelekom.telsiz.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TaskModel(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("qrcode") val qrcode: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("finish_date") val finishDate: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("minute") val minute: Int,
    @SerializedName("completed_location_count") val completedLocationCount: Int,
    @SerializedName("total_location_count") val totalLocationCount: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("deleted_at") val deletedAt: String,
    @SerializedName("explanation_id") val explanationId: String,
    @SerializedName("location_id") val locationId: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("is_checkin_available") val isCheckinAvailable: Boolean,
    @SerializedName("nfc_reader_active") val nfcReaderActive : String?,
    @SerializedName("barcode_reader_active") val barcodeReaderActive : String?,
    @SerializedName("todos") var todos: List<Int>?,
    @SerializedName("completed_date") val completedDate: String?
) : BaseModel {
    var isTodoAdded: Boolean =false
    var isQrCodeValid: Boolean = false
    var isNcfValid: Boolean = false
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskModel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
