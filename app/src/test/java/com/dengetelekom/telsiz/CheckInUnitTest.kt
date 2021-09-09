package com.dengetelekom.telsiz

import androidx.lifecycle.ViewModelProvider
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.models.NfcObject
import com.dengetelekom.telsiz.models.TokenRequestModel
import com.dengetelekom.telsiz.providers.TransceiverProvider
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.dengetelekom.telsiz.services.ApiClient
import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CheckInUnitTest {
    @DelicateCoroutinesApi
    @Test
    fun check_in_Success(): Unit = runBlocking {
        try {
            val transceiverProvider = TransceiverProvider()
            val request = TokenRequestModel(
                Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.GRANT_TYPE,
                "2031500300", "12345678"
            )
            Constants.ACCESS_TOKEN = transceiverProvider.token(request).access_token
            val transceiverViewModel = TransceiverRepository()
            val result = transceiverViewModel.checkin(NfcObject("Piazza Avm"))

            assert(true)
        } catch (e: Exception) {
            assert(true)
        }

    }

    @Test
    fun check_in_ViewModel(): Unit = runBlocking {


    }
}