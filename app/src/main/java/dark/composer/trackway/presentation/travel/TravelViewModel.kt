package dark.composer.trackway.presentation.travel

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.services.LocationService
import dark.composer.trackway.data.utils.FirebaseDatabaseHelper
import dark.composer.trackway.data.utils.LastLatLng
import dark.composer.trackway.data.utils.TravelInterface
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TravelViewModel : ViewModel() {
    private val db by lazy {
        Firebase.database
    }

    private val locationChannel = Channel<LatLng>()
    val locationFlow = locationChannel.receiveAsFlow()

    private val sendChannel = Channel<TravelData>()
    val sendFlow = sendChannel.receiveAsFlow()

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
                        sendChannel.send(TravelData(key, name))
                    }
                }
        }
    }

    private val travel = MutableLiveData<LatLng>()
    val travelLiveDate:LiveData<LatLng> = travel

    fun readTravel(path:String,name: String,travelName:String) {
        FirebaseDatabaseHelper().readLast(path,name, travelName,object : LastLatLng{
            override fun last(data: List<HistoryData>) {
                if (data.isNotEmpty()){
                    Log.d("LLLL", "last: ${data.last().lat} ${data.last().lon}")
                }
                    travel.value = LatLng(data.last().lat,data.last().lon)
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