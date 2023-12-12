package com.example.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.domain.TrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelTracker
@Inject
constructor(private val repository: TrackerRepository): ViewModel() {

fun deleteData(){
    viewModelScope.launch {
        repository.deleteDataFromRoom()
    }
}
}