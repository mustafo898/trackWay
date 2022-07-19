package dark.composer.trackway.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat
import java.util.*
import kotlin.math.*

class LocationHelper {

    private var listener: ((lat: Double, lon: Double, speed: Double, time: Long,distance:Double) -> Unit)? = null

    //    var previousLocation: Location? = null
    fun setOnChangeLocation(f: (lat: Double, lon: Double, speed: Double, time: Long,distance:Double) -> Unit) {
        listener = f
    }

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var l: Location? = null
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
                            Date().time,
                            distance(lastLocation!!.latitude,lastLocation!!.longitude,p0.lastLocation!!.latitude,p0.lastLocation!!.longitude)
                        )
                    } else if (lastLocation!!.latitude != p0.lastLocation!!.latitude
                        || lastLocation!!.longitude != p0.lastLocation!!.longitude
                    ) {
                        lastLocation = p0.lastLocation!!
                        listener?.invoke(
                            p0.lastLocation!!.latitude,
                            p0.lastLocation!!.longitude,
                            lastLocation?.speed!!.toDouble(),
                            Date().time,
                            distance(lastLocation!!.latitude,lastLocation!!.longitude,p0.lastLocation!!.latitude,p0.lastLocation!!.longitude)
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
//                previousLocati    on = p0.result!!

                if (lastLocation == null) {
                    lastLocation = p0.result!!
                    listener?.invoke(
                        p0.result!!.latitude,
                        p0.result!!.longitude,
                        p0.result.speed.toDouble(),
                        Date().time,
                        distance(lastLocation!!.latitude,lastLocation!!.longitude,p0.result!!.latitude,p0.result!!.longitude)
                    )
                } else if (lastLocation!!.latitude != p0.result!!.latitude
                    || lastLocation!!.longitude != p0.result!!.longitude
                ) {
                    lastLocation = p0.result!!
                    listener?.invoke(
                        p0.result!!.latitude,
                        p0.result!!.longitude,
                        p0.result.speed.toDouble(),
                        Date().time,
                        distance(lastLocation!!.latitude,lastLocation!!.longitude,p0.result!!.latitude,p0.result!!.longitude))
                }
            } catch (e: Exception) {
//                log(e.toString())
            }
        }
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


    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist*1.609344
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}
