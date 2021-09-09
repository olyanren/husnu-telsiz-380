package com.dengetelekom.telsiz


import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dengetelekom.telsiz.models.*
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.dengetelekom.telsiz.retrofitcoroutines.remote.NetworkResponse
import com.dengetelekom.telsiz.ui.ui.login.LoginFormState

class TransceiverViewModel(private val repository: TransceiverRepository) : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    private val _refreshData = MutableLiveData<String>()
    private val _previousData = MutableLiveData<String>()

    val loginFormState: LiveData<LoginFormState> = _loginForm
    val refreshState: LiveData<String> = _refreshData
    val previousState: LiveData<String> = _previousData

    private val TAG = "TransceiverViewModel"
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun refresh() { _refreshData.value = "REFRESHED" }
    fun previousRecord() { _previousData.value = "PREVIOUS" }


    fun showRefreshButton() {
        _refreshData.value = "VISIBLE"
    }

    fun hideRefreshButton() {
        _refreshData.value = "GONE"
    }


    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun token(username: String, password: String) = liveData {
        emit(Resource.loading(data = null))
        try {
            val request = TokenRequestModel(
                Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.GRANT_TYPE,
                username, password
            )
            emit(Resource.success(data = repository.token(request)))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

    fun tasks() = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp=repository.tasks()) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.data))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.error(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }

        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun notifications() = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp=repository.notifications()) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.data))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.error(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }

        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun previousNotification() = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.previousNotification()))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun previousTask() = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.previousTask()))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun checkin(nfc:NfcObject) = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp=repository.checkin(nfc)) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.message))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.error(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }

        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun checkout(nfc:NfcObject) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.checkout(nfc)))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun explanationTitles() = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.explanationTitles()))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

    fun todos() = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.todos()))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

    fun addExplanation(request: ExplanationAddRequestModel) = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp= repository.addExplanation(request)) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.data))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.apiError(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun addUrgentNotification(request: UrgentNotificationAddRequestModel) = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp= repository.addUrgentNotification(request)) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.message))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.apiError(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

    fun uploadPhoto(taskId: Int,locationId:String, fileUri: String) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.uploadPhoto(taskId,locationId, fileUri)))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun uploadUrgentNotificationPhoto(explanation:String, fileUri: String) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.uploadUrgentNotificationPhoto(explanation, fileUri)))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun addTodos(request: ToDoAddRequestModel) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.addTodos(request)))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

    fun complete(request: TaskCompleteRequestModel) = liveData {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.complete(request)))
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

}
