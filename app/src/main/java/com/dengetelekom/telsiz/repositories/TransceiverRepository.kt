package com.dengetelekom.telsiz.repositories

import com.dengetelekom.telsiz.models.*
import com.dengetelekom.telsiz.providers.TransceiverProvider


class TransceiverRepository : BaseRepository {
    private var provider = TransceiverProvider()

    suspend fun token(request: TokenRequestModel) = provider.token(request)
    suspend fun tasks() = provider.tasks()
    suspend fun previousTask() = provider.previousTask()
    suspend fun checkin(nfc:NfcObject) = provider.checkin(nfc)
    suspend fun checkout(nfc:NfcObject) = provider.checkout(nfc)
    suspend fun explanationTitles() = provider.explanationTitles()
    suspend fun todos() = provider.todos()
    suspend fun addExplanation(request: ExplanationAddRequestModel) = provider.addExplanation(request)
    suspend fun addUrgentNotification(request: UrgentNotificationAddRequestModel) = provider.addUrgentNotification(request)
    suspend fun addTodos(request: ToDoAddRequestModel) = provider.addTodos(request)
    suspend fun complete(request: TaskCompleteRequestModel) = provider.complete(request)
    suspend fun uploadPhoto(taskId: Int,locationId:String, fileUri: String) = provider.uploadPhoto(taskId,locationId,fileUri)
    suspend fun uploadUrgentNotificationPhoto(explanation:String, fileUri: String) = provider.uploadUrgentNotificationPhoto(explanation, fileUri)


}