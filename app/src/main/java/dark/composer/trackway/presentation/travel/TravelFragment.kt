package dark.composer.trackway.presentation.travel

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dark.composer.trackway.R
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentTravelBinding
import dark.composer.trackway.presentation.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class TravelFragment : BaseFragment<FragmentTravelBinding>(FragmentTravelBinding::inflate) {
    private lateinit var shared: SharedPref
    lateinit var viewModel: TravelViewModel
    var name = ""
    private val PERMISSION_REQUEST_CODE = 123
    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.isMyLocationEnabled = true
        viewModel.travelLiveDate.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.lifecycle.whenStarted {
                        viewModel.travelLiveDate.observeForever { list ->
                            list.forEach {
                                options.add(LatLng(it.lat, it.lon))
                                Log.d("EEEE", "latlng: ${it.lat} ${it.lon}")
                            }
                            googleMap.addPolyline(options)
                        }
                    }
                }
            }
            Log.d("EEEEE", ": $it}")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.locationFlow.collect {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17.5f))
                }
            }
        }

        googleMap.setOnMyLocationButtonClickListener {
            if (!checkPermission()) {
                checkPermission()
            }
            false
        }

        if (shared.getTheme() == 0) {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        } else if (shared.getTheme() == 1) {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        } else if (shared.getTheme() == 2) {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        if (shared.getMode()) {
            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.map_style_night
                    )
                )
                Toast.makeText(requireContext(), "${shared.getMode()}", Toast.LENGTH_SHORT).show()
                if (!success) {
                    Log.e("fail!", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("catch", "Can't find style. Error: ", e)
            }
        } else {
            Toast.makeText(requireContext(), "${shared.getMode()}", Toast.LENGTH_SHORT).show()
            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.map_style_light
                    )
                )
                if (!success) {
                    Log.e("fail!", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("catch", "Can't find style. Error: ", e)
            }
        }

        if (name.isNotEmpty()) {
            viewModel.send(
                name,
                requireContext(),
                requireActivity(),
                shared.getUsername().toString()
            )
        }

        binding.searchImage.setOnClickListener {
            val location = binding.search.text.toString()
            if (location.isNotEmpty()) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val address: List<Address> = geocoder.getFromLocationName(location, 1)
                if (address.isNotEmpty()) {
                    val latLng = LatLng(address[0].latitude, address[0].longitude)
                    val marketOptions = MarkerOptions()
                    marketOptions.position(latLng)
                    googleMap.addMarker(marketOptions)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED
            -> {
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                Toast.makeText(
                    requireContext(),
                    "Please give me access !!!",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ),
                    PERMISSION_REQUEST_CODE
                )
                return false
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    requireContext(),
                    "Please give me access !!!",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ),
                    PERMISSION_REQUEST_CODE
                )
                return false
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ),
                    PERMISSION_REQUEST_CODE
                )
                return false
            }
        }
    }

    private fun draw(it: LatLng): PolylineOptions {
        val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        options.add(it)
        Log.d("Draw", "latlng: ${it.latitude} ${it.longitude}")
        return options
    }

    override fun onViewCreate() {
        shared = SharedPref(
            requireContext()
        )

        viewModel = ViewModelProvider(this)[TravelViewModel::class.java]

        if (checkPermission()) {
            viewModel.getLocation(requireContext())
        } else {
            checkPermission()
        }

        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME", "")
        }
        if (name.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
            }
        }

        if (LocationService.isServiceRunningInForeground(
                requireActivity(),
                LocationService::class.java
            )
        ) {
            binding.finish.visibility = View.VISIBLE
        } else {
            binding.finish.visibility = View.GONE
        }

        binding.finish.setOnClickListener {
            LocationService.stopLocationService(requireActivity())
            navController.navigate(
                R.id.action_travelFragment_to_historyDetailsFragment,
                bundleOf("TRAVEL_NAME" to name)
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.successFlow.collect {
                    if (it) {
                        viewModel.readTravel("history", shared.getUsername().toString(), name)
                    } else {
                        Toast.makeText(requireContext(), "Service $it", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (checkPermission()) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
            viewModel.getLocation(requireActivity())
        } else {
            checkPermission()
        }
    }
}