package com.dengetelekom.telsiz.repositories

import com.dengetelekom.telsiz.models.*
import com.dengetelekom.telsiz.providers.TransceiverProvider


class TransceiverRepository : BaseRepository {
    private var provider = TransceiverProvider()

    suspend fun token(request: TokenRequestModel) = provider.token(request)
    suspend fun tasks() = provider.tasks()
    suspend fun previousTask() = provider.previousTask()
    suspend fun explanationTitles() = provider.explanationTitles()
    suspend fun todos() = provider.todos()
    suspend fun addExplanation(request: ExplanationAddRequestModel) = provider.addExplanation(request)
    suspend fun addTodos(request: ToDoAddRequestModel) = provider.addTodos(request)
    suspend fun complete(request: TaskCompleteRequestModel) = provider.complete(request)
    suspend fun uploadPhoto(taskId: Int,locationId:String, fileUri: String) = provider.uploadPhoto(taskId,locationId,fileUri)


}