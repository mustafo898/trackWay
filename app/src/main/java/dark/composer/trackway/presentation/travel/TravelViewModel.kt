package dark.composer.trackway.presentation.travel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.services.LocationService
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
        val key = db.getReference("travel").push().key ?: ""
        db.getReference("travel").child(username).child(name)
            .setValue(TravelData(key, name))
            .addOnCompleteListener {
                val intent = Intent(context, LocationService::class.java)
                intent.putExtra("TRAVEL_ID", key)
                intent.putExtra("USER_NAME", username)
                intent.putExtra("NAME", name)
                LocationService.startLocationService(activityContext,key,name,username)
                viewModelScope.launch {
                    sendChannel.send(TravelData(key, name))
                }
            }
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
//        val locationListener = LocationListener { location ->
//            val latitude = location.latitude
//            val longitude = location.longitude
//            val latLng = LatLng(latitude,longitude)
//            viewModelScope.launch {
//                locationChannel.send(latLng)
//            }
//        }
//        try {
//        } catch (ex: SecurityException) {
//            Toast.makeText(context, "Fehler bei der Erfassung!", Toast.LENGTH_SHORT).show()
//        }
    }
}