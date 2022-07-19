package dark.composer.trackway.presentation.history.detail

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import dark.composer.trackway.R
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentHistoryDetailsBinding
import dark.composer.trackway.presentation.BaseFragment
import dark.composer.trackway.presentation.history.HistoryViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class HistoryDetailsFragment :
    BaseFragment<FragmentHistoryDetailsBinding>(FragmentHistoryDetailsBinding::inflate) {

    private lateinit var viewModel: HistoryViewModel
    var name = ""
    private lateinit var sharedPref: SharedPref

    override fun onViewCreate() {
        sharedPref = SharedPref(requireContext())
        viewModel = ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME", "")
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.detailsMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        viewModel.readTravel("history", sharedPref.getUsername().toString(), name)
    }

    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.isMyLocationEnabled = true
        val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.travelFlow.collect { list ->
                    list.forEach {
                        options.add(LatLng(it.lat, it.lon))
                        Log.d("EEEE", "latlng: ${it.lat} ${it.lon}")
                    }
                    load(list)
                    googleMap.addPolyline(options)
                    val p1 = LatLng(list.first().lat, list.first().lon)
                    val p2 = LatLng(list.last().lat, list.last().lon)

                    googleMap.addMarker(MarkerOptions().position(p1))
                    googleMap.addMarker(MarkerOptions().position(p2))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p1, 16f))
                }
            }
        }
    }

    private fun load(list: List<HistoryData>) {
        var count = 0
        val firsTime = list.first().historyTime
        var lasTime = list.last().historyTime
        var speed = 0.0
        val calendar = Calendar.getInstance()
        var distance = 0.0
        var d = 0.0
        for (i in 0 until list.size - 1) {
            d = SphericalUtil.computeDistanceBetween(
                LatLng(list[i].lat, list[i].lon),
                LatLng(list[i + 1].lat, list[i + 1].lon)
            )
            distance += d
            count += 1
            speed += list[i].historyAvgSpeed
        }

        lasTime -= firsTime
        calendar.timeInMillis = lasTime
        speed /= count

        binding.distance.text = String.format("%.3f", distance / 1000)
        binding.speed.text = String.format("%.1f", speed)
        binding.time.text = SimpleDateFormat("dd-MM-yyyy").format(
            calendar.time
        )
        binding.travelName.text = name
    }

    private fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return radius * c
    }
}

