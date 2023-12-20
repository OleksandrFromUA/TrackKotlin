package com.example.auth.presentation


import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class AuthViewModel
@Inject
constructor(private val repository: AuthRepository): ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //  private var job: Job? = null

    val email = ObservableField<String>("")
    val password = ObservableField<String>("")

   private suspend fun registration(email: String, password: String): CompletableDeferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Unit>()

            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { deferred.complete(Unit) }
                    .addOnFailureListener { exception -> deferred.completeExceptionally(exception) }
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }

            deferred
        }
    }
  /* private suspend fun registration(email: String, password: String) {
       return withContext(Dispatchers.IO) {
           try {
               firebaseAuth.createUserWithEmailAndPassword(email, password)
                   .addOnSuccessListener {
                       // успех
                   }
                   .addOnFailureListener { exception ->
                       // ошибка
                       exception.printStackTrace()
                   }
           } catch (e: Exception) {
               // другие исключения
               e.printStackTrace()
           }
       }
   }*/

    suspend fun signIn(email: String, password: String): CompletableDeferred<Unit> {
        return withContext(Dispatchers.IO) {
            val deferredSignIn = CompletableDeferred<Unit>()
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { deferredSignIn.complete(Unit) }
                    .addOnFailureListener { exeption ->
                        deferredSignIn.completeExceptionally(
                            exeption
                        )
                    }
            } catch (e: Exception) {
                deferredSignIn.completeExceptionally(e)
            }
            deferredSignIn
        }
    }

    suspend fun saveInRoom(email: String) = withContext(Dispatchers.IO) {
        repository.saveToLocal(email)
    }

    suspend fun registrationAndSaveUser(email: String, password: String) {
        viewModelScope.launch {
            registration(email, password).await()
            val currentUid = getCurrentUserUid()
            repository.saveUserToRoom(currentUid, email)
        }

    }


    override fun onCleared() {
        super.onCleared()
    }

   private suspend fun getCurrentUserUid(): String {
            val currentUser = firebaseAuth.currentUser
          return  currentUser?.uid ?: throw Exception("Current user is null")

    }
}