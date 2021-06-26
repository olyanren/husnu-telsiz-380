package com.dengetelekom.telsiz.services


import com.dengetelekom.telsiz.models.*
import com.dengetelekom.telsiz.retrofitcoroutines.remote.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface TransceiverService {

    @POST("oauth/token")
    suspend fun token(@Body request: TokenRequestModel): TokenResponseModel

    @GET("tasks")
    suspend fun tasks(): NetworkResponse<TaskResponseModel, RetrofitError>

    @GET("tasks/previous")
    suspend fun previousTask(): TaskResponseModel

    @GET("explanation-titles")
    suspend fun explanationTitles(): ExplanationTitleResponseModel

    @GET("todo-titles")
    suspend fun todos(): TodoTitleResponseModel

    @PUT("tasks/explanations")
    suspend fun addExplanation(@Body request: ExplanationAddRequestModel): NetworkResponse<TaskResponseModel, RetrofitError>

    @PUT("tasks/todos")
    suspend fun addTodos(@Body request: ToDoAddRequestModel): ApiResponseModel

    @PUT("tasks/complete")
    suspend fun complete(@Body request: TaskCompleteRequestModel): ApiResponseModel

    @Multipart
    @POST("tasks/upload-photos")
    suspend fun uploadPhoto(@Part("task_id") taskId: RequestBody?,
                            @Part("location_id") locationId: RequestBody?,
                            @Part file: MultipartBody.Part?): ApiResponseModel
}