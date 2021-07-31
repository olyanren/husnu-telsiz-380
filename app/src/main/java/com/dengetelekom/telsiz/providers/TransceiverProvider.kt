package com.dengetelekom.telsiz.providers


import android.util.Log
import com.dengetelekom.telsiz.Constants
import com.dengetelekom.telsiz.models.*
import com.dengetelekom.telsiz.retrofitcoroutines.remote.NetworkResponse
import com.dengetelekom.telsiz.services.ApiClient
import com.dengetelekom.telsiz.services.TransceiverService
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File


class TransceiverProvider {

    suspend fun token(request: TokenRequestModel): TokenResponseModel {
        val service = getBaseClient().create(TransceiverService::class.java)
        Log.d("DengeTelsiz", Gson().toJson(request))
        return service.token(request)
    }

    private fun getBaseClient(): Retrofit = ApiClient.getClient(Constants.API_BASE_URL)
    private fun getApiClient(): Retrofit = ApiClient.getClientBearer(Constants.API_URL, Constants.ACCESS_TOKEN)

    suspend fun tasks(): NetworkResponse<TaskResponseModel, RetrofitError> {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.tasks()
    }
    suspend fun previousTask(): TaskResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.previousTask()
    }
    suspend fun explanationTitles(): ExplanationTitleResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.explanationTitles()
    }
    suspend fun todos(): TodoTitleResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.todos()
    }
    suspend fun addExplanation(request: ExplanationAddRequestModel): NetworkResponse<TaskResponseModel, RetrofitError> {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.addExplanation(request)
    }
    suspend fun addUrgentNotification(request: UrgentNotificationAddRequestModel): NetworkResponse<TaskResponseModel, RetrofitError> {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.addUrgentNotification(request)
    }
    suspend fun addTodos(request: ToDoAddRequestModel): ApiResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.addTodos(request)
    }
    suspend fun complete(request: TaskCompleteRequestModel): ApiResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        return service.complete(request)
    }
    suspend fun uploadPhoto(taskId: Int,locationId:String, fileUri: String): ApiResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        val file =  File(fileUri)
        val requestFile: RequestBody = RequestBody.create(
                MediaType.parse("image/jpeg"),
                file
        )
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

        val taskIdBody = RequestBody.create(MultipartBody.FORM, taskId.toString())
        val locationIdBody = RequestBody.create(MultipartBody.FORM, locationId)
        return service.uploadPhoto(taskIdBody,locationIdBody,body)
    }
    suspend fun uploadUrgentNotificationPhoto(explanation:String,fileUri: String): ApiResponseModel {
        val service = getApiClient().create(TransceiverService::class.java)
        val file =  File(fileUri)
        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse("image/jpeg"),
            file
        )
        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)


        val explanationBody = RequestBody.create(MultipartBody.FORM, explanation)
        return service.uploadUrgentNotificationPhoto(explanationBody,body)
    }
}