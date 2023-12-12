package com.example.auth.presentation

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.auth.R
import com.example.auth.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch


class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding.authViewModel = authViewModel
        binding.lifecycleOwner = this

        auth = FirebaseAuth.getInstance()

        binding.SIGNIN.setOnClickListener {
            val email = binding.InputNameText.text.toString()
            val password = binding.InputPasswordText.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    lifecycleScope.launch {
                        try {
                            authViewModel.signIn(email, password).await()
                            Log.i("Auth", "User successfully signed in")
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.user_in_the_system), Toast.LENGTH_SHORT
                            ).show()
                            authViewModel.saveInRoom(email)
                            Log.i("Auth", "User successfully saved in Room")

                        } catch (e: Exception) {
                            e.printStackTrace()
                           // Log.e("Auth", "Error during authentication or saving to Room", e)

                            if (e is FirebaseAuthInvalidUserException) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.user_not_registered_please_register),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.error_occurred), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_email), Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.email_and_password_fields_are_empty), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.signUp.setOnClickListener {
            val email = binding.InputNameText.text.toString()
            val password = binding.InputPasswordText.text.toString()
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    lifecycleScope.launch {
                        try {
                            authViewModel.registrationAndSaveUser(email, password)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.registration_are_successful), Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.registration_failed), Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_email), Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.email_and_password_fields_are_empty), Toast.LENGTH_SHORT
                ).show()
            }

        }

        return binding.root
    }
}