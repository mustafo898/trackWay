package dark.composer.trackway.data.utils

import androidx.navigation.ActivityNavigator
import com.google.android.gms.maps.model.LatLng

class Helper {
    fun getUri(p1:LatLng,p2:LatLng,destinationMode:String){
        val strOrigin = "origin=${p1.latitude},${p1.longitude}"
        val strDest = "dest=${p2.latitude},${p2.latitude}"
        val mode = "mode=$destinationMode"
        val parameters = "$strOrigin&$strDest&$mode"
        val output = "json"
        val uri = "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyDAw6rFNjZ6evDTUKXFtzfSOOw7RC6x4Bg"
    }
}