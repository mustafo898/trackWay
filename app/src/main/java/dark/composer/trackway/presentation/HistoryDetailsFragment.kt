package dark.composer.trackway.presentation

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dark.composer.trackway.R
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.utils.SharedPref
import dark.composer.trackway.databinding.FragmentHistoryDetailsBinding
import kotlinx.coroutines.launch


class HistoryDetailsFragment :
    BaseFragment<FragmentHistoryDetailsBinding>(FragmentHistoryDetailsBinding::inflate) {
    private lateinit var viewModel: HistoryViewModel
    var name = ""
    private lateinit var sharedPref: SharedPref
    private lateinit var list :List<HistoryData>

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = false
    }

    override fun onViewCreate() {
        sharedPref = SharedPref(requireContext())
        viewModel = ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME", "")
        }
        list = ArrayList()

        val mapFragment = childFragmentManager.findFragmentById(R.id.detailsMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.travelFlow.collect {
                    Log.d("DDDD", "details: $it")
                    val barData = createChartData(it)
                    prepareChartData(barData)
                    list = it
                }
            }
        }

        viewModel.readTravel("history", sharedPref.getUsername().toString(), name)
    }

    private fun prepareChartData(data: BarData) {
        data.setValueTextSize(12f)
        binding.barChart.data = data
        binding.barChart.invalidate()
    }

    private fun createChartData(it: List<HistoryData>): BarData {
        val set1 = BarDataSet(getBarEntries(it), sharedPref.getUsername().toString())
        set1.setColors(
            intArrayOf(
                R.color.one,
                R.color.two,
                R.color.three,
                R.color.four,
            ), requireContext()
        )
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        return BarData(dataSets)
    }

    private fun getBarEntries(it: List<HistoryData>): ArrayList<BarEntry> {
        val barEntriesArrayList = ArrayList<BarEntry>()
        it.forEach { t ->
            Log.d(
                "WWWWW",
                "initChart: ${t.historyTime.toString().substring(0, 2)}  ${t.historyAvgSpeed}"
            )
            barEntriesArrayList.add(
                BarEntry(
                    t.historyAvgSpeed.toString().substring(0, 2).toFloat(),
                    t.historyTime.toString().substring(0, 5).toFloat()
                )
            )
        }
        return barEntriesArrayList
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }


}

