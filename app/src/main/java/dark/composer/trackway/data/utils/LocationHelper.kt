package dark.composer.trackway.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.util.*

class LocationHelper {

    private var listener: ((lat: Double, lon: Double, speed: Double, time: Long) -> Unit)? = null

    //    var previousLocation: Location? = null
    fun setOnChangeLocation(f: (lat: Double, lon: Double, speed: Double, time: Long) -> Unit) {
        listener = f
    }

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult) {
                try {
                    val speed: Double = lastLocation?.let { lastLocation ->
                        // Convert milliseconds to seconds
                        val elapsedTimeInSeconds =
                            (p0.lastLocation!!.time - lastLocation.time) / 1_000.0
                        val distanceInMeters = lastLocation.distanceTo(p0.lastLocation!!)
//                        var t = 2435
                        // Speed in m/s
                        distanceInMeters / elapsedTimeInSeconds
                    } ?: 0.0
//                    previousLocation = p0.lastLocation!!

                    if (lastLocation == null) {
                        lastLocation = p0.lastLocation!!
                        listener?.invoke(
                            p0.lastLocation!!.latitude,
                            p0.lastLocation!!.longitude,
                            lastLocation?.speed!!.toDouble(),
                            Date().time
                        )
                    } else if (lastLocation!!.latitude != p0.lastLocation!!.latitude
                        || lastLocation!!.longitude != p0.lastLocation!!.longitude
                    ) {
                        lastLocation = p0.lastLocation!!
                        listener?.invoke(
                            p0.lastLocation!!.latitude,
                            p0.lastLocation!!.longitude,
                            speed,
                            Date().time
                        )
                    }
                } catch (e: Exception) {
//                    log(e.toString())
                }

            }
        }

        locationRequest = LocationRequest.create()
        locationRequest.interval = 8000
        locationRequest.fastestInterval = 4000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, Looper.myLooper()
        )

        mFusedLocationClient.lastLocation.addOnCompleteListener { p0 ->
            try {
                val speed: Double = lastLocation?.let { lastLocation ->
                    // Convert milliseconds to seconds
                    val elapsedTimeInSeconds = (p0.result!!.time - lastLocation.time) / 1_000.0
                    val distanceInMeters = lastLocation.distanceTo(p0.result!!)
                    // Speed in m/s
                    distanceInMeters / elapsedTimeInSeconds
                } ?: 0.0
//                previousLocation = p0.result!!

                if (lastLocation == null) {
                    lastLocation = p0.result!!
                    listener?.invoke(
                        p0.result!!.latitude,
                        p0.result!!.longitude,
                        p0.result.speed.toDouble(),
                        Date().time
                    )
                } else if (lastLocation!!.latitude != p0.result!!.latitude
                    || lastLocation!!.longitude != p0.result!!.longitude
                ) {
                    lastLocation = p0.result!!
                    listener?.invoke(
                        p0.result!!.latitude,
                        p0.result!!.longitude,
                        p0.result.speed.toDouble(),
                        Date().time
                    )
                }
            } catch (e: Exception) {
//                log(e.toString())
            }
        }
    }
}
