package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class LoginRegViewModel @Inject constructor (
    application: Application,
    private val repository: PostRepository,
    private val appAuth: AppAuth,
    @ApplicationContext
    val appContext: Context,
) : AndroidViewModel(application) {

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    val data = appAuth.data
        .asLiveData()

    fun login (login: String, pass: String) = viewModelScope.launch {
        try {
            val token = repository.getToken(login, pass)
            appAuth.setToken(token)
            Toast.makeText(appContext, appContext.getString(R.string.logged_in) + login, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(appContext, R.string.wrong_pas_login, Toast.LENGTH_SHORT).show()
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun satPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }


    fun sendRegistration(login: String, pass: String, name: String)= viewModelScope.launch {
        try {
            if (photo.value != null) {
                val token = repository.registrationWithPhoto(login, pass, name, photo.value!!)
                appAuth.setToken(token)
                Toast.makeText(appContext, appContext.getString(R.string.signed_in) + login, Toast.LENGTH_SHORT).show()
            } else {
                val token = repository.registration(login, pass, name)
                appAuth.setToken(token)
                Toast.makeText(appContext, appContext.getString(R.string.signed_in) + login, Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception) {
            Toast.makeText(appContext, R.string.error_registr, Toast.LENGTH_SHORT).show()
            _dataState.value = FeedModelState(error = true)
        }
    }

    val isAuthorized: Boolean
        get() = appAuth.data.value != null
}