package dark.composer.trackway.data.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dark.composer.trackway.R
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.utils.LocationHelper
import dark.composer.trackway.presentation.MainActivity

class LocationService : Service() {

    private var locationHelper: LocationHelper? = null
    private val db by lazy {
        Firebase.database
    }

    companion object {
        fun startLocationService(
            context: Activity,
            travelId: String,
            name: String,
            userName: String
        ) {
            if (isServiceRunningInForeground(context, LocationService::class.java)) {
                stopLocationService(context)
            }
            val intent = Intent(context, LocationService::class.java)
            intent.putExtra("TRAVEL_ID", travelId)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("NAME", name)
            context.startService(intent)
        }

        fun stopLocationService(context: Activity) {
            val intent = Intent(context, LocationService::class.java)
            context.stopService(intent)
        }

        fun isServiceRunningInForeground(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return service.foreground
                }
            }
            return false
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.extras?.let {
            val travelId = it.getString("TRAVEL_ID") ?: ""
            val userName = it.getString("USER_NAME") ?: ""
            val name = it.getString("NAME") ?: ""
            locationHelper?.setOnChangeLocation { lat, lon, speed, time ->
                val key = db.getReference("history").push().key ?: ""
                db.getReference("history").child(userName).child(name).child(time.toString())
                    .setValue(HistoryData(key, speed, time, travelId, lat, lon))
                Log.d(
                    "History",
                    "onStartCommand: ${HistoryData(key, speed, time, travelId, lat, lon)}"
                )
            }
        }
        startForeground(1, notificationToDisplayServiceInfor())
        return START_STICKY
    }

    override fun onCreate() {
        locationHelper = LocationHelper()
        locationHelper?.getLocation(this)
        super.onCreate()
    }

    private fun notificationToDisplayServiceInfor(): Notification {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Simple Foreground Service")
            .setContentText("Explain about the service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(p0: Intent?) = null

}