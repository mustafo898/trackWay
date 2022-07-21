package dark.composer.trackway.data.utils

import android.telephony.SignalThresholdInfo
import com.google.firebase.database.*
import dark.composer.trackway.data.local.HistoryData
import dark.composer.trackway.data.local.TravelData

class FirebaseDatabaseHelper() {
//    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var mReference:DatabaseReference
    private var travelList = mutableListOf<HistoryData>()
    private var travelNameList = mutableListOf<String>()
    private var travelName = mutableListOf<TravelData>()
    private var key = mutableListOf<String>()

    fun readDatabase(path:String,name:String,status:TravelInterface){
        mReference = FirebaseDatabase.getInstance().getReference(path)
        mReference.child(name).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s1 in snapshot.children) {
                    s1.key?.let {
                        travelNameList.add(it)
                    }
                    for (s2 in s1.children){
                        s2.key?.let {
                            key.add(it)
                        }
                        val travelData: HistoryData? = s2.getValue(HistoryData::class.java)
                        travelData?.let {
                            travelList.add(it)
                        }
                    }
                }
                status.loaded(travelList,key,travelNameList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readNameDatabase(path:String,name:String,travelName:String,status:TravelInterface){
        mReference = FirebaseDatabase.getInstance().getReference(path)
        mReference.child(name).child(travelName).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s1 in snapshot.children) {
                    s1.key?.let {
                        key.add(it)
                    }
                    val travelData: HistoryData? = s1.getValue(HistoryData::class.java)
                    travelData?.let {
                        travelList.add(it)
                    }
                }
                status.travelLoaded(travelList,travelName,key)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readName(name:String,status:GetName){
        mReference = FirebaseDatabase.getInstance().getReference("travel")
        mReference.child(name).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s1 in snapshot.children) {
                    s1.key?.let {
                        key.add(it)
                    }
                    val travelData: TravelData? = s1.getValue(TravelData::class.java)
                    travelData?.let {
                        travelName.add(it)
                    }
                }
                status.name(travelName)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun readLast(path:String,name:String,travelName:String,status:LastLatLng){
        mReference = FirebaseDatabase.getInstance().getReference(path)
        val list = mutableListOf<HistoryData>()
        mReference.child(name).child(travelName).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s1 in snapshot.children) {
                    val travelData: HistoryData? = s1.getValue(HistoryData::class.java)
                    travelData?.let {
                        list.add(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        status.last(list)
    }
}