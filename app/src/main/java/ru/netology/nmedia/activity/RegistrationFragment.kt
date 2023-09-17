package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.viewmodel.LoginRegViewModel
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.model.PhotoModel

class RegistrationFragment : Fragment() {

    private val photoPickerContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> Toast.makeText(
                    requireContext(),
                    getString(R.string.pick_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
                Activity.RESULT_OK -> {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    viewLoginModel.satPhoto(PhotoModel(uri, uri.toFile()))
                }
            }
        }

    private val viewLoginModel: LoginRegViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {

            fun textStatMaker(bool: Boolean){
                textFieldPassAgainIn.isEnabled = bool
                textFieldNameIn.isEnabled = bool
                textFieldLoginIn.isEnabled = bool
                textFieldPassIn.isEnabled = bool
            }

            buttonAddAvatar.setOnClickListener {
                ImagePicker.with(this@RegistrationFragment)
                    .galleryOnly()
                    .crop()
                    .createIntent(photoPickerContract::launch)
            }

            buttonSignup.setOnClickListener {
                val name = textFieldNameIn.text.toString()
                val pass = textFieldPassIn.text.toString()
                val passConf = textFieldPassAgainIn.text.toString()
                val login = textFieldLoginIn.text.toString()
                if (pass == passConf) {
                    viewLoginModel.sendRegistration(login, pass, name)
                    textStatMaker(false)
                } else {
                    Toast.makeText(context, getString(R.string.not_match), Toast.LENGTH_SHORT).show()
                    textFieldPassAgainIn.setText("")
                    textFieldPassIn.setText("")
                    textStatMaker(true)
                }
            }

            viewLoginModel.photo.observe(viewLifecycleOwner) { photo ->
                if (photo == null) {
                    binding.avatar.isGone = true
                    return@observe
                }
                avatar.isVisible = true
                avatar.setImageURI(photo.uri)
            }

            viewLoginModel.dataState.observe(viewLifecycleOwner) {
                textStatMaker(true)
            }

            viewLoginModel.data.observe(viewLifecycleOwner) {
                if (viewLoginModel.isAuthorized) {
                    textStatMaker(true)
                    findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }
}