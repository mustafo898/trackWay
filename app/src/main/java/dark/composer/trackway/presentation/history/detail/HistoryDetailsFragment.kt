package dark.composer.trackway.presentation.history.detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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
        val first = Calendar.getInstance()
        val time = Calendar.getInstance()
        val last = Calendar.getInstance()
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
        speed /= count

        last.timeInMillis = lasTime
        binding.endTime.text = SimpleDateFormat().format(last.time)
        first.timeInMillis = firsTime
        binding.startTime.text = SimpleDateFormat().format(first.time)

        binding.time.text = time(firsTime.toString(),lasTime.toString())
        binding.distance.text = String.format("%.3f", distance / 1000)
        binding.speed.text = String.format("%.1f", speed)
        binding.travelName.text = name
    }

    fun time(l1: String, l2: String): String {
        val format = SimpleDateFormat("yy/MM/dd HH:mm:ss")

        var d1: Date? = null
        var d2: Date? = null
        try {
            d1 = format.parse(l1)
            d2 = format.parse(l2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val diff = d2!!.time - d1!!.time
        val diffSeconds = diff / 1000
        val diffMinutes = diff / (60 * 1000)
        val diffHours = diff / (60 * 60 * 1000)
        println("Time in seconds: $diffSeconds seconds.")
        println("Time in minutes: $diffMinutes minutes.")
        println("Time in hours: $diffHours hours.")
        if (diffMinutes > 3600) {
            return "$diffHours : $diffMinutes : $diffSeconds"
        } else {
            return "$diffMinutes : $diffSeconds"
        }
    }
}

