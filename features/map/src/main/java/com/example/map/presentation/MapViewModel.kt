package com.example.map.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocationData
import com.example.map.domain.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel:ViewModel() {
    private val mapRepository = MapRepository()

    private val _coordinatesFlow = MutableStateFlow<List<LocationData>>(emptyList())
   // val coordinatesFlow: Flow<List<LocationData>> = _coordinatesFlow.asStateFlow()
   
    init {
        viewModelScope.launch {
            mapRepository.getCoordinatesForDay(System.currentTimeMillis())
                .collect { currentCoordinates ->
                    _coordinatesFlow.value = currentCoordinates
                }
        }
    }

    /*init {
        viewModelScope.launch {
            mapRepository.getCoordinatesForDay(System.currentTimeMillis())
        }
    }*/


     fun updatingInterface(selectedDate: Long) {
        viewModelScope.launch {
            mapRepository.getCoordinatesForDay(selectedDate)
                .collect { updatedCoordinates ->
                    _coordinatesFlow.value = updatedCoordinates
                }
        }
    }
/*suspend fun updatingInterface(selectedDate: Long){
    viewModelScope.launch {
        mapRepository.getCoordinatesForDay(selectedDate)
    }
}*/

    fun logOut(){
        viewModelScope.launch {
            mapRepository.logOutFromFirebase()
        }
    }

}