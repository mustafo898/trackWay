package dark.composer.trackway.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.local.TravelData
import dark.composer.trackway.data.utils.FirebaseDatabaseHelper
import dark.composer.trackway.data.utils.GetName
import dark.composer.trackway.data.utils.TravelInterface
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val travelChannel = Channel<List<HistoryData>>()
    val travelFlow = travelChannel.receiveAsFlow()

    private val keyChannel = Channel<List<String>>()
    val keyFlow = keyChannel.receiveAsFlow()

    private val nameChannel = Channel<List<TravelData>>()
    val nameFlow = nameChannel.receiveAsFlow()

    fun readTravels(name: String) {
        FirebaseDatabaseHelper().readName(name, object : GetName {
            override fun name(f: List<TravelData>) {
                viewModelScope.launch {
//                    travelChannel.send(list)
//                    keyChannel.send(key)
                    nameChannel.send(f)
                }
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