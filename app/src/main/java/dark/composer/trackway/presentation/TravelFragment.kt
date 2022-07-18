package dark.composer.trackway.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dark.composer.trackway.R
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentTravelBinding
import kotlinx.coroutines.launch
import java.util.*


class TravelFragment : BaseFragment<FragmentTravelBinding>(FragmentTravelBinding::inflate) {
    private lateinit var shared: SharedPref
    lateinit var viewModel: TravelViewModel
    var name = ""
    private val PERMISSION_REQUEST_CODE = 123

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.locationFlow.collect {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                }
            }
        }

        if (name.isNotEmpty()) {
//            viewModel.send(name,requireContext(),requireContext(),shared.getUsername().toString())
            checkPermission(name,shared.getUsername().toString())
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

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        checkPermission()
//    }
//
//    private fun checkPermission() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(), arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//
//                    ),
//                12345
//            )
//        }
//    }

    private fun checkPermission(name: String, username: String){
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
//                        &&
//                        ContextCompat.checkSelfPermission(
//                            requireContext(),
//                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                        ) == PackageManager.PERMISSION_GRANTED
            -> {
               viewModel.send(name,requireContext(),requireActivity(),username)
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
            }
        }
    }

    override fun onViewCreate() {
        shared = SharedPref(
            requireContext()
        )
//        checkPermission()

        viewModel = ViewModelProvider(this)[TravelViewModel::class.java]
        viewModel.getLocation(requireContext())

        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME", "")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.sendFlow.collect{
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                    Log.i("Travel Data", "onViewCreate: $it")
                }
            }
        }
        binding.finish.setOnClickListener {
            Toast.makeText(requireContext(), "${shared.getTravelName()} finished", Toast.LENGTH_SHORT).show()
            activity?.stopService(Intent(requireContext(), LocationService::class.java))
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}