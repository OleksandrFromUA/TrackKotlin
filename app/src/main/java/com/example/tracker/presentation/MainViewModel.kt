package com.example.tracker.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.UserData
import com.example.tracker.domain.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(private val repository: MainRepository): ViewModel() {

   val isLogged: LiveData<List<UserData>>? = repository.getUserById()?.asLiveData()

   /* private val _userById = MutableLiveData<List<User>>()
    val userById: LiveData<List<User>> get() = _userById

    fun getUserById() {
        viewModelScope.launch {
            repository.getUserById()?.collect() {
                _userById.value = it
            }
        }
    }*/
}