package com.example.auth.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

   // val password: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
        val email = MutableLiveData<String>("")
        val password = MutableLiveData<String>("")
}