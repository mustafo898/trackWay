package dark.composer.trackway.presentation.travel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dark.composer.trackway.R
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentTravelBinding
import dark.composer.trackway.presentation.BaseFragment
import kotlinx.coroutines.launch
import java.util.*


class TravelFragment : BaseFragment<FragmentTravelBinding>(FragmentTravelBinding::inflate) {
    private lateinit var shared: SharedPref
    lateinit var viewModel: TravelViewModel
    var name = ""
    private val PERMISSION_REQUEST_CODE = 123

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true
        googleMap.isBuildingsEnabled = true

        if (shared.getTheme() == 0) {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        } else if (shared.getTheme() == 1) {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        } else if (shared.getTheme() == 2) {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        googleMap.setOnMyLocationButtonClickListener {
            if (!checkPermission()) {
                checkPermission()
            }
            false
        }

        GoogleMapOptions().liteMode(true)

        if (shared.getMode()){
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
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
        }else{
            Toast.makeText(requireContext(), "${shared.getMode()}", Toast.LENGTH_SHORT).show()
//            GoogleMapOptions().liteMode(false)
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.locationFlow.collect {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17.5f))
                }
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
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f))
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

    override fun onViewCreate() {
        shared = SharedPref(
            requireContext()
        )

        checkPermission()

        viewModel = ViewModelProvider(this)[TravelViewModel::class.java]

        if (checkPermission()) {
//            viewModel.getLocation(requireContext())
        } else {
            checkPermission()
        }

        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME", "")
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
            activity?.stopService(Intent(requireActivity(), LocationService::class.java))
//            LocationService.stopLocationService(requireActivity())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.sendFlow.collect {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                    Log.i("Travel Data", "onViewCreate: $it")
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}