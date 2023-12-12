package com.example.tracker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tracker.MyWorker
import com.example.tracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = firebaseAuth.currentUser
        /////val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                } else {
                    navController.navigate(R.id.action_trackerFragment_to_authFragment)
                }
            })
        } else {
            navController.navigate(R.id.action_trackerFragment_to_authFragment)
        }
    }
}