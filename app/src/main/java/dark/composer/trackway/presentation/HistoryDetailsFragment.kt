package dark.composer.trackway.presentation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
//    private lateinit var list :List<HistoryData>

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true
        val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel.travelFlow.collect {list->
                    list.forEach{
                        options.add(LatLng(it.lat,it.lon))
                        Log.d("EEEE", "latlng: ${it.lat} ${it.lon}")
                    }
                    googleMap.addPolyline(options)
                    val p1 = LatLng(list.first().lat,list.first().lon)
                    val p2 = LatLng(list.last().lat,list.last().lon)
                    googleMap.addMarker(MarkerOptions().position(p1))
                    googleMap.addMarker(MarkerOptions().position(p2))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p1,16f))
                    Log.d("DDDD", "details: $list")
                    val barData = createChartData(list)
                    prepareChartData(barData)
                }
            }
        }
    }

    override fun onViewCreate() {
        sharedPref = SharedPref(requireContext())
        viewModel = ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
        val bundle: Bundle? = this.arguments
        bundle?.let {
            name = it.getString("TRAVEL_NAME", "")
        }
//        list = ArrayList()

        val mapFragment = childFragmentManager.findFragmentById(R.id.detailsMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


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
}

