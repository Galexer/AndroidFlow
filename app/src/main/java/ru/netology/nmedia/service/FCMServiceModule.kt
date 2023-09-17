package ru.netology.nmedia.service

import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class FCMServiceModule {

    @Provides
    fun provideGoogleApiAvailability() : GoogleApiAvailability =
        GoogleApiAvailability.getInstance()

    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()
}