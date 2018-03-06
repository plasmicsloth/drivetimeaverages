package dom.drivetimeaverages

class Trip {

    var id:Int = 0
    var path:String = ""
    var tripLights:Int = 0
    var milliseconds:Int = 0

    constructor(id: Int, path:String, tripLights: Int, milliseconds: Int) {
        this.id = id
        this.path = path
        this.tripLights = tripLights
        this.milliseconds = milliseconds
    }

    constructor(path: String, tripLights: Int, milliseconds: Int) {
        this.path = path
        this.tripLights = tripLights
        this.milliseconds = milliseconds
    }
}
