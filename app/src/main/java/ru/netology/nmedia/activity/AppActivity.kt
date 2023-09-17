package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dialog.DialogFrafment
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity () : AppCompatActivity(R.layout.activity_app) {

    @Inject
    lateinit var appAuth: AppAuth
    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationsPermission()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        checkGoogleApiAvailability(googleApiAvailability, firebaseMessaging)

        var oldMenuProvider: MenuProvider? = null
        val viewModel: AuthViewModel by viewModels()

        val dialog = DialogFrafment(getString(R.string.logOut))
        val support = supportFragmentManager

        viewModel.data.observe(this) {
            oldMenuProvider?.let(::removeMenuProvider)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)
                    val authorized = viewModel.isAuthorized
                    if(authorized) {
                        menu.setGroupVisible(R.id.authorized, true)
                        menu.setGroupVisible(R.id.unauthorized, false)
                    } else {
                        menu.setGroupVisible(R.id.authorized, false)
                        menu.setGroupVisible(R.id.unauthorized, true)
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when(menuItem.itemId) {
                        R.id.auth -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_loginApp)
                            //appAuth.setToken(Token(5L, "x-Token"))
                            true
                        }
                        R.id.register -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_registration)
                            true
                        }
                        R.id.logout -> {
                            dialog.show(support, "tag")
                            dialog.no.observe(this@AppActivity){
                                if(it) {
                                    appAuth.clearAuth()
                                }
                            }
                            true
                        }
                        else -> false
                    }

            }.apply {
                    oldMenuProvider = this
            }, this)
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
    fun checkGoogleApiAvailability(
        googleApiAvailability: GoogleApiAvailability,
        firebaseMessaging: FirebaseMessaging
    ) {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}