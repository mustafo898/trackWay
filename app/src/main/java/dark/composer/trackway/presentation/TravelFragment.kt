package dark.composer.trackway.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.R
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentTravelBinding
import java.util.*


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
            name = it.getString("TRAVEL_NAME", "")
        }

        if (name.isNotEmpty()) {
           send(name)
        }

        binding.searchImage.setOnClickListener {
            val location = binding.search.text.toString()
            if (location.isNotEmpty()){
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val address:List<Address> = geocoder.getFromLocationName(location,1)
                if (address.isNotEmpty()){
                    val latLng = LatLng(address[0].latitude,address[0].longitude)
                    val marketOptions = MarkerOptions()
                    marketOptions.position(latLng)
                    googleMap.addMarker(marketOptions)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f))
                }
            }
        }
    }

    fun send(name:String){
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }

    private fun checkPermission() {
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
        }
    }

    override fun onViewCreate() {
        shared = SharedPref(
            requireContext()
        )
        checkPermission()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}