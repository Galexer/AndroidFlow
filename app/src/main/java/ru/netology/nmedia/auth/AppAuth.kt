package ru.netology.nmedia.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.warker.SendPushTokenWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val TOKEN_KEY = "TOKEN_KEY"
    private val ID_KEY = "ID_KEY"

    private val pref: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data = MutableStateFlow<Token?>(null)
    val data = _data.asStateFlow()

    init {
        val token = pref.getString(TOKEN_KEY, null)
        val id = pref.getLong(ID_KEY, 0)

        if (token == null || id == 0L) {
            pref.edit { clear() }
        } else {
            _data.value = Token(id, token)
        }
    }

    fun sendPushToken(token: String? = null) {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                SendPushTokenWorker.NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<SendPushTokenWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(SendPushTokenWorker.TOKEN_KEY, token)
                            .build()
                    )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            )
    }

    @Synchronized
    fun setToken(token: Token) {
        _data.value = token
        pref.edit {
            putString(TOKEN_KEY, token.token)
            putLong(ID_KEY, token.id)
        }
        sendPushToken()
    }

    @Synchronized
    fun clearAuth() {
        _data.value = null
        pref.edit { clear() }
        sendPushToken()
    }
}