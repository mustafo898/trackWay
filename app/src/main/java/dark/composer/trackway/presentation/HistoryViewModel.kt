package dark.composer.trackway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.utils.FirebaseDatabaseHelper
import dark.composer.trackway.data.utils.TravelInterface
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val travelChannel = Channel<List<HistoryData>>()
    val travelFlow = travelChannel.receiveAsFlow()

    private val keyChannel = Channel<List<String>>()
    val keyFlow = keyChannel.receiveAsFlow()

    private val nameChannel = Channel<List<String>>()
    val nameFlow = nameChannel.receiveAsFlow()

    fun readTravels(path:String,name: String) {
        FirebaseDatabaseHelper().readDatabase(path,name, object : TravelInterface {
            override fun loaded(list: List<HistoryData>, key: List<String>,travelName:List<String>) {
                viewModelScope.launch {
//                    travelChannel.send(list)
//                    keyChannel.send(key)
                    nameChannel.send(travelName)
                }
            }

            override fun travelLoaded(
                travelDate: List<HistoryData>,
                travelName: String,
                key: List<String>
            ) {

            }
        })
    }



    fun readTravel(path:String,name: String,travelName:String) {
        FirebaseDatabaseHelper().readNameDatabase(path,name, travelName, object : TravelInterface {
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
                viewModelScope.launch {
                    travelChannel.send(list1)
//                    keyChannel.send(key)
                }
            }
        })
    }
}