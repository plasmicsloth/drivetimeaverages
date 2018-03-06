package dom.drivetimeaverages

class Path {

    var id: Int = 0
    var pathName: String? = null
    var possibleLights = 0

    constructor(id: Int, pathName: String, possibleLights: Int) {
        this.id = id
        this.pathName = pathName
        this.possibleLights = possibleLights
    }

    constructor(pathName: String, possibleLights: Int) {
        this.pathName = pathName
        this.possibleLights = possibleLights
    }
}