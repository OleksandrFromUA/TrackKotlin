package com.example.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.data.LocationData
import com.example.map.MyDialogCalendar
import com.example.map.R
import com.example.map.databinding.FragmentMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel
    private lateinit var myGoogleMap: GoogleMap

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarMap)

        if (areLocationPermissionsGranted()) {
            initializeMap()
        } else {
            requestLocationPermissions()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBarMenu()
    }

    /*@Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap()
            } else {

            }
        }
    }*/
    private fun initializeMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun areLocationPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermissions() {
        if (isInternetAvailable()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE
                ),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        } else {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
            //  Toast.makeText(requireContext(), "Интернет соединение отсутствует, пройдите в настройки и включите интернет",
            //     Toast.LENGTH_SHORT).show()
        }
    }
   private fun isInternetAvailable(): Boolean {
       val connectivityManager =
           requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

       if (connectivityManager != null) {
           val network = connectivityManager.activeNetwork
           val capabilities = connectivityManager.getNetworkCapabilities(network)

           return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
       }

       return false
   }
    private fun setAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_meny_map, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.actionLogout -> {
                        viewModel.logOut()
                        //переход на фрагмент аутентификации
                        return true
                    }

                    R.id.calendar_ -> {
                        showCalendarDialog()
                        return true
                    }

                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun showCalendarDialog() {
        val dialogCalendar = MyDialogCalendar()
        dialogCalendar.show(childFragmentManager, "MyDialogCalendar")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        myGoogleMap?.let {
            val current = FirebaseAuth.getInstance().currentUser
            if (current != null) {
                childFragmentManager.setFragmentResultListener(
                    MyDialogCalendar.SelectedDay,
                    this
                ) { _, result ->
                    val newDateResult = result.getLong(MyDialogCalendar.SelectedDay)

                    viewModel.updatingInterface(newDateResult)//прослушивание изменений

                        /* if (coordinates != null) {
                        updateMapAndShowCoordinatesOnMap(coordinates)
                    } else {
                        Log.e("MyTag", "Coordinates are null")
                    }*/
                }
            } else {
                Log.e("teg", "User is not authenticated")
            }
        } ?: Log.e("MyTag", "myGoogleMap == null")
    }

    @SuppressLint("MissingPermission")
    private fun updateMapAndShowCoordinatesOnMap(coordinates: List<LocationData>?) {
        if (!coordinates.isNullOrEmpty()) {
            myGoogleMap.clear()
            val polylinePoints = mutableListOf<LatLng>()

            for (newCoordinate in coordinates) {
                val latLng = LatLng(newCoordinate.latitude, newCoordinate.longitude)
                polylinePoints.add(latLng)

                myGoogleMap.addMarker(MarkerOptions().position(latLng).title("Marker google map"))
            }

            if (polylinePoints.size > 1) {
                val polylineOptions = PolylineOptions()
                    .addAll(polylinePoints)
                    .width(5f)
                    .color(Color.BLUE)

                myGoogleMap.addPolyline(polylineOptions)
            }
        }
    }
}