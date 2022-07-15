package dark.composer.trackway.data.local

class HistoryData {
    var id: String = ""
    var historyAvgSpeed: Double = 0.0
    var historyTime: Long = 0
    var travelId: String = ""
    var lat: Double = 0.0
    var lon: Double = 0.0
    constructor()
    constructor(
        id: String,
        historyAvgSpeed: Double,
        historyTime: Long,
        travelId: String,
        lat: Double,
        lon: Double
    ) {
        this.id = id
        this.historyAvgSpeed = historyAvgSpeed
        this.historyTime = historyTime
        this.travelId = travelId
        this.lat = lat
        this.lon = lon
    }


}