package com.example.auth.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auth.R
import com.example.auth.databinding.FragmentAuthBinding


class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
   // private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

}