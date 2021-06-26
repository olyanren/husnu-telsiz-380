package com.dengetelekom.telsiz.factories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.repositories.TransceiverRepository

class TransceiverViewModelFactory(private val repository: TransceiverRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransceiverViewModel(
            repository
        ) as T
    }
}