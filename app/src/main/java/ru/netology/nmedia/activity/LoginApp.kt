package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLoginAppBinding
import ru.netology.nmedia.viewmodel.LoginRegViewModel


class FragmentLoginAppBinding : Fragment() {

    private val viewLoginModel: LoginRegViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginAppBinding.inflate(
            inflater,
            container,
            false
        )


        with(binding) {
            buttonLogin.setOnClickListener {
                var login = textFieldLoginIn.text.toString()
                var pass = textFieldPassIn.text.toString()
                var result = viewLoginModel.login(login, pass)
                textFieldPassIn.setText("")
                textFieldLoginIn.isEnabled = false
                textFieldPassIn.isEnabled = false
            }

            buttonSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginApp_to_registration)
            }

            viewLoginModel.dataState.observe(viewLifecycleOwner) {
                textFieldLoginIn.isEnabled = true
                textFieldPassIn.isEnabled = true
            }

            viewLoginModel.data.observe(viewLifecycleOwner) {
                if (viewLoginModel.isAuthorized) {
                    textFieldLoginIn.isEnabled = true
                    textFieldPassIn.isEnabled = true
                    findNavController().navigateUp()
                }
            }
        }

        return binding.root
    }
}