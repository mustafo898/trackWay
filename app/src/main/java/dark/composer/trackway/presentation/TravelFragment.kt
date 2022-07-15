package dark.composer.trackway.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.R
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentTravelBinding


class TravelFragment : BaseFragment<FragmentTravelBinding>(FragmentTravelBinding::inflate) {
    private lateinit var shared: SharedPref
    private val db by lazy {
        Firebase.database
    }
    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.isMyLocationEnabled = true
        var name = ""
        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME","")
        }

        if (name.isNotEmpty()){
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,

                        ),
                    12345
                )
            } else {
                Toast.makeText(requireContext(), "checkPermissions", Toast.LENGTH_SHORT).show()
                val key = db.getReference("travel").push().key ?: ""
                db.getReference("travel").child(shared.getUsername().toString()).child(name)
                    .setValue(TravelData(key, name))
                    .addOnCompleteListener {
                        shared.setTravelId(key)
                        val intent = Intent(requireContext(), LocationService::class.java)
                        intent.putExtra("TRAVEL_ID", key)
                        intent.putExtra("USER_NAME", shared.getUsername().toString())
                        intent.putExtra("NAME", name)
                        requireActivity().startService(intent)
                    }
            }
        }
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Tashkent"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

//        binding.selectPlace.setOnClickListener {
//
//
//        }

    }

    override fun onViewCreate() {
        shared = SharedPref(
            requireContext()
        )
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}