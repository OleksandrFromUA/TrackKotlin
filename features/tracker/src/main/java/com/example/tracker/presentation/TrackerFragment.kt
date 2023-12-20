package com.example.tracker.presentation

import android.Manifest
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.tracker.MyForegroundService
import com.example.tracker.R
import com.example.tracker.databinding.FragmentTrackerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackerFragment() : Fragment() {
    private lateinit var viewModel: ViewModelTracker
    private lateinit var binding: FragmentTrackerBinding
    private var isTracking: Boolean = false
    private var isGpsEnabled: Boolean = false
    private lateinit var locationManager: LocationManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ViewModelTracker::class.java)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.Start.setOnClickListener { _ ->
            run {
                if (!isTracking) {
                    requestLocationAndNotificationPermission()
                } else {
                    stopTracker()
                }
            }
        }

        return binding.root
    }

    private fun startTracker() {
        val serviceIntent = Intent(requireContext(), MyForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        }
        isTracking = true
        binding.Start.text = getString(R.string.stop)
    }

    private fun stopTracker() {
        val serviceIntentStop = Intent(requireContext(), MyForegroundService::class.java)
        requireContext().stopService(serviceIntentStop)
        isTracking = false
        binding.Start.text = getString(R.string.start)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBarMenu()
    }

    private fun setAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_meny, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_logout -> {
                        viewModel.deleteData()
                            //переход на AuthFragment
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }


    private fun showGpsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.activate_gps_in_the_settings_to_use_this_feature))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun requestNotificationPermission() {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.areNotificationsEnabled()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.Настройки_уведомлений_доступны), Toast.LENGTH_SHORT
            ).show()
        } else {
            try {
                val intent = Intent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.Настройки_уведомлений_недоступны), Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun requestLocationAndNotificationPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
        requestPermissionLauncher.launch(permissions)
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
            val isGranted = permissionResult.all { entry ->
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    entry.key
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (isGranted) {
                if (checkGpsAndStartService()) {
                    startTracker()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.gps_is_off), Toast.LENGTH_SHORT
                    ).show()
                    showGpsDialog()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.the_user_did_not_grant_both_permissions), Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun checkGpsAndStartService(): Boolean {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (isGpsEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestNotificationPermission()
            }
            Toast.makeText(
                requireContext(),
                getString(R.string.gps_in_on), Toast.LENGTH_SHORT
            ).show()
            true
        } else {
            showGpsDialog()
            false
        }
    }
}
