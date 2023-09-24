package ru.netology.nmedia.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R

class DialogFrafment(val mess: String) : DialogFragment() {
    private val _no = MutableLiveData(false)
    val no: LiveData<Boolean>
        get() = _no

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        return activity?.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(mess)
                .setPositiveButton(getString(R.string.yes)) { dialog, id ->
                    _no.value = true
                    dialog.cancel()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, id ->
                    _no.value = false
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
