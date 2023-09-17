package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class LoginRegViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    val data = AppAuth.getInstance().data
        .asLiveData()

    fun login (login: String, pass: String) = viewModelScope.launch {
        try {
            val token = repository.getToken(login, pass)
            AppAuth.getInstance().setToken(token)
            Toast.makeText(context, context.getString(R.string.logged_in) + login, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, R.string.wrong_pas_login, Toast.LENGTH_SHORT).show()
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
                AppAuth.getInstance().setToken(token)
                Toast.makeText(context, context.getString(R.string.signed_in) + login, Toast.LENGTH_SHORT).show()
            } else {
                val token = repository.registration(login, pass, name)
                AppAuth.getInstance().setToken(token)
                Toast.makeText(context, context.getString(R.string.signed_in) + login, Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception) {
            Toast.makeText(context, R.string.error_registr, Toast.LENGTH_SHORT).show()
            _dataState.value = FeedModelState(error = true)
        }
    }

    val isAuthorized: Boolean
        get() = AppAuth.getInstance().data.value != null
}