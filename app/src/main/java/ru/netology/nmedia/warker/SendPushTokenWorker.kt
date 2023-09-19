package ru.netology.nmedia.warker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject

class SendPushTokenWorker @Inject constructor(
    @ApplicationContext
    val appContext: Context,
    val param: WorkerParameters,
) : CoroutineWorker(
    appContext,
    param
) {

    companion object {
        const val NAME = "SendPushTokenWorker"
        const val TOKEN_KEY = "TOKEN_KEY"
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiServices(): ApiService
    }

    override suspend fun doWork(): Result {
        val token = this.inputData.getString(TOKEN_KEY)
        return try {
            val request = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
            val entryPoint =
                EntryPointAccessors.fromApplication(appContext, AppAuthEntryPoint::class.java)
            entryPoint.getApiServices().sendPushToken(request)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

}

