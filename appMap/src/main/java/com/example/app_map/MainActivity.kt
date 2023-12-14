package com.example.app_map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.app_map.databinding.ActivityMainBinding
import com.example.map.InterfaceForNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), InterfaceForNavigation {
    private lateinit var navController: NavController
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = firebaseAuth.currentUser
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_main)
binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_map) as NavHostFragment
        navController = navHostFragment.navController
        setupObservation()
    }

    private fun setupObservation(){
        currentUser?.let {
            navController.navigate(R.id.action_track_to_map)
        }?: navController.navigate(R.id.action_map_to_authFragment)
    }

    override fun navigateToAuthFragment(){
       navController.navigate(R.id.action_map_to_authFragment)
    }
}