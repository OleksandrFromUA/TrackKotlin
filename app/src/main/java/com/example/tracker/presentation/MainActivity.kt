package com.example.tracker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.tracker.MyWorker
import com.example.tracker.R
import com.example.tracker.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity(){
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = firebaseAuth.currentUser
    private lateinit var binding: ActivityMainBinding
    //private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // val request = OneTimeWorkRequestBuilder<MyWorker>().build()
       // WorkManager.getInstance(applicationContext).enqueue(request)

       // WorkManager.getInstance().beginUniqueWork("UniqueWork", ExistingWorkPolicy.REPLACE,
         //   OneTimeWorkRequest.Companion.from(MyWorker::class.java)).enqueue()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupObservation()
      MyWorker.startMyWorker(this)
    }
     private fun setupObservation() {
        if (currentUser != null) {
            viewModel.isLogged?.observe(this, Observer { isLogged ->
                if (isLogged != null) {
                    navController.navigate(R.id.action_authFragment_to_trackerFragment)
                    Log.e("MainActivity","переход на trackerFragment")
                } else {
                    navController.navigate(R.id.action_trackerFragment_to_authFragment)
                    Log.e("MainActivity","переход на authFragment")
                }
            })
        } else {
            navController.navigate(R.id.action_trackerFragment_to_authFragment)
        }
    }

    /*   private fun setupObservation() {
           if (currentUser != null) {
               viewModel.isLogged?.observe(this, Observer { isLogged ->
                   isLogged?.let {
                       navController.navigate(R.id.action_authFragment_to_trackerFragment)
                       Log.e("MainActivity","переход на trackerFragment")
                   }?:navController.navigate(R.id.action_trackerFragment_to_authFragment)
                   Log.e("MainActivity","переход на authFragment")

       })
   }

       }*/
}