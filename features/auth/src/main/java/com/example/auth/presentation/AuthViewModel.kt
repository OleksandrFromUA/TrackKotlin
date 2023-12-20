package com.example.auth.presentation


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class AuthViewModel
@Inject
constructor(private val repository: AuthRepository): ViewModel() {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val email = ObservableField<String>("")
    val password = ObservableField<String>("")


    private suspend fun registration(email: String, password: String): Unit = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signIn(email: String, password: String): Unit = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun saveInRoom(email: String): Unit = withContext(Dispatchers.IO) {
        repository.saveToLocal(email)
    }


    suspend fun registrationAndSaveUser(email: String, password: String) {
        try {
            registration(email, password)
            val currentUid = getCurrentUserUid()
            repository.saveUserToRoom(currentUid, email)
        } catch (e: Exception) {
            throw e
        }
    }


   private suspend fun getCurrentUserUid(): String {
            val currentUser = firebaseAuth.currentUser
          return  currentUser?.uid ?: throw Exception("Current user is null")

    }
}