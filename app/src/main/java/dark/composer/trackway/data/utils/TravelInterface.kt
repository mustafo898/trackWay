package dark.composer.trackway.data.utils

import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.local.TravelData

interface TravelInterface {
    fun loaded(list:List<HistoryData>,key:List<String>,travelName:List<String>)
    fun travelLoaded(travelDate:List<HistoryData>,travelName:String,key:List<String>)
}