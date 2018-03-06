package dom.drivetimeaverages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val request_code = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            // Launch Drive activity
            val i = Intent(this,DrivingActivity::class.java)
            // initialize variable to receive new drive time
            startActivityForResult(i, request_code)
        }

        refreshAverages()
    }
    override fun onRestart() {
        super.onRestart()
        //refreshAverages()
    }
    override fun onResume() {
        super.onResume()
        refreshAverages()
    }

    private fun refreshAverages() {
        txtAverages.setText("")

        // populate text list with paths
        val listOfPaths : List<Path> = PathDBHandler(this, null, null, 1).findPath("all") ?: emptyList()
        // safely exit if empty
        if (listOfPaths.isEmpty()) return

        for (eachPath in listOfPaths) {
            // get all trips for this path

            val pathName = eachPath.pathName
            val possibleLights = eachPath.possibleLights
            val pathID = eachPath.id

            // get all trips for this path
            val tripHandler = TripDBHandler(this, null, null, 1)
            val listOfTrips: List<Trip>? = tripHandler.findTrips(pathName!!)
            var tripCount = 0
            var totalTime = 0
            var totalLights = 0

            if (listOfTrips?.count()!! > 0) {
                for (eachTrip in listOfTrips) {
                    if (eachTrip.path == pathName) {
                        tripCount++
                        totalTime += eachTrip.milliseconds
                        totalLights += eachTrip.tripLights
                    }
                }
                if (tripCount > 0) {
                    val avgMilliseconds = totalTime / tripCount
                    var Seconds = (avgMilliseconds / 1000).toInt()
                    val Minutes = Seconds / 60
                    Seconds = Seconds % 60
                    val Milliseconds = (avgMilliseconds % 10).toInt()
                    val displayTime = ("$Minutes:" + String.format("%02d", Seconds) + "." + String.format("%01d", Milliseconds))
                    val avgLights:Long = (totalLights / tripCount).toLong()
                    // TODO: get avglights to 1 decimal

                    // add averages to text
                    txtAverages.append("$pathName ($pathID) -  Average Time: $displayTime " + System.getProperty("line.separator"))
                    txtAverages.append("   Averages Lights: " + avgLights + " of $possibleLights   |   Total Trips: $tripCount" + System.getProperty("line.separator"))
                    txtAverages.append(System.getProperty("line.separator"))

                    // add averages to list
                    //val progressBar: ProgressBar = ProgressBar(this)
                    //val adapter = ArrayAdapter(this, listAverages, "$pathName ($pathID) -  Average Time: $displayTime")
                    //listAverages.adapter(adapter)

                }
            }
            tripHandler.close()
        }
        //pathHandler.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        // do stuff?
        if (resultCode == Activity.RESULT_OK) {
            // display the latest time
            var displayTime = data?.getStringExtra("displaytime")
            txtLatestLabel.visibility = 0.toInt()
            txtLatestTime.text = displayTime
            txtLatestTime.visibility = 0.toInt()
            toast("Trip finished in $displayTime")
            // add time to averages
            refreshAverages()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_managePaths -> {
                // send us to the paths activity
                val i = Intent(this, PathsActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
