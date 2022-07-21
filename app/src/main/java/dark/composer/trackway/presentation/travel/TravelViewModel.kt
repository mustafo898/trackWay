package dark.composer.trackway.presentation.travel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.FirebaseDatabaseHelper
import dark.composer.trackway.data.utils.TravelInterface
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TravelViewModel : ViewModel() {
    private val db by lazy {
        Firebase.database
    }

    private val travelChannel = MutableLiveData<List<HistoryData>>()
    val travelLiveDate: LiveData<List<HistoryData>> = travelChannel

    private val locationChannel = Channel<LatLng>()
    val locationFlow = locationChannel.receiveAsFlow()

    private val successChannel = Channel<Boolean>()
    val successFlow = successChannel.receiveAsFlow()

    fun send(name: String, context: Context, activityContext: Activity, username: String) {
        if (!LocationService.isServiceRunningInForeground(
                activityContext,
                LocationService::class.java
            )
        ) {
            val key = db.getReference("travel").push().key ?: ""
            db.getReference("travel").child(username).child(name)
                .setValue(TravelData(key, name))
                .addOnCompleteListener {
                    val intent = Intent(context, LocationService::class.java)
                    intent.putExtra("TRAVEL_ID", key)
                    intent.putExtra("USER_NAME", username)
                    intent.putExtra("NAME", name)
                    LocationService.startLocationService(activityContext, key, name, username)
                    viewModelScope.launch {
                        successChannel.send(true)
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        successChannel.send(false)
                    }
                }
        }
    }

//    private val travel = MutableLiveData<LatLng>()
//    val travelLiveDate:LiveData<LatLng> = travel

//    fun readTravel(path:String,name: String,travelName:String) {
//        FirebaseDatabaseHelper().readLast(path,name, travelName,object : LastLatLng{
//            override fun last(data: List<HistoryData>) {
//                if (data.isNotEmpty()){
//                    Log.d("LLLL", "last: ${data.last().lat} ${data.last().lon}")
//                }
//                    travel.value = LatLng(data.last().lat,data.last().lon)
//            }
//        })
//    }

    fun readTravel(path: String, name: String, travelName: String) {
        FirebaseDatabaseHelper().readNameDatabase(path, name, travelName, object : TravelInterface {
            override fun loaded(
                list: List<HistoryData>,
                key: List<String>,
                travelName: List<String>
            ) {

            }

            override fun travelLoaded(
                list1: List<HistoryData>,
                travelName: String,
                key: List<String>
            ) {
                travelChannel.value = list1
            }
        })
    }

    fun getLocation(context: Context) {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val latitude =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.latitude
        val longitude =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.longitude

        Log.d("test", "Latitute: $latitude ; Longitute: $longitude")

        val latLng = LatLng(latitude!!, longitude!!)
        viewModelScope.launch {
            locationChannel.send(latLng)
        }
    }
}