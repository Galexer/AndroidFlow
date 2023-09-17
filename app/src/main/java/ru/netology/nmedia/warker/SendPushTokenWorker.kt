package ru.netology.nmedia.warker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken

class SendPushTokenWorker(
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

    override suspend fun doWork(): Result {
        val token = this.inputData.getString(TOKEN_KEY)
        return try {
            val request = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
            PostsApi.service.sendPushToken(request)
            Result.success()
        } catch (e: Exception ){
            e.printStackTrace()
            Result.retry()
        }
    }

}

