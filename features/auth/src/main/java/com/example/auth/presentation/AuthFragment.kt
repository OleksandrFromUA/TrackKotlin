package com.example.auth.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.auth.R
import com.example.auth.databinding.FragmentAuthBinding


class AuthFragment : Fragment() {
private lateinit var binding: FragmentAuthBinding
private lateinit var authViewModel: AuthViewModel

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = DataBindingUtil.inflate(inflater,R.layout.fragment_auth,container, false)
         authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
         binding.authViewModel = authViewModel
         binding.lifecycleOwner = this

        /* binding.apply {
             authViewModel = this@AuthFragment.authViewModel
             lifecycleOwner = this@AuthFragment.viewLifecycleOwner
         }*/

        return binding.root
    }
}