package dom.drivetimeaverages

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_paths.*
import kotlinx.android.synthetic.main.content_paths.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class PathsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paths)
        setSupportActionBar(toolbar)

        updatePathView()
    }

    private fun updatePathView() {
        txtPaths.text = ""

        // populate list of all current paths to start
        val paths = PathDBHandler(this,null,null,1).findPath("all")
        if (paths!!.count() > 0) {
            for (path in paths) {
                txtPaths.append(path.pathName + "(" + path.id + "), lights: " + path.possibleLights)
                txtPaths.append(System.getProperty("line.separator"))
            }
        } else txtPaths.text = ""
        val trips = TripDBHandler(this,null,null,1).findTrips("all")
        txtTripCount.text = "Trip count: " + trips!!.count().toString()
    }

    fun newPath(v: View) {
        // start a db handler for the path
        if (editName.text.length == 0) {
            alert("Name is required").show()
            editName.requestFocus()
        } else if (editLights.text.length == 0) {
            alert("Number of lights required").show()
            editLights.requestFocus()
        } else {
            val pathHandler = PathDBHandler(this, null, null, 1)
            val lights = Integer.parseInt(editLights.text.toString())
            val path = Path(editName.text.toString(), lights)

            pathHandler.addPath(path)
            pathHandler.close()
            Snackbar.make(v, "Path ${path.pathName} added!", Snackbar.LENGTH_SHORT)

            updatePathView()
            resetForm()
        }
    }

    fun removePath(v: View) {
        if (editName.text.isEmpty()) {
            alert("Name is required").show()
            editName.requestFocus()
        } else {
            val pathHandler = PathDBHandler(this, null, null, 1)
            val path = editName.text.toString()
            pathHandler.deletePath(path)
            pathHandler.close()
            // delete all trips associated with said path
            val tripHandler = TripDBHandler(this, null, null, 1)
            if (tripHandler.deleteTrips(path)) Snackbar.make(v, "Trips for $path deleted", Snackbar.LENGTH_SHORT)
            else Snackbar.make(v, "No trips deleted", Snackbar.LENGTH_SHORT)

            updatePathView()
            resetForm()
        }
    }

    private fun resetForm() {
        editName.setText("")
        editLights.setText("")
        editName.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_paths, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_resetPaths -> {
                // delete all paths and trips
                alert("Are you sure you want to delete paths and trips?") {
                    yesButton {
                        val resetPaths = PathDBHandler(this@PathsActivity, null, null, 1).resetPaths()
                        val resetTrips = TripDBHandler(this@PathsActivity, null, null, 1).resetTrips()
                        if (resetPaths && resetTrips) toast("Reset complete")
                            else toast ("Oops, issue resetting")
                    }
                    noButton { toast("OK, nothing deleted") }
                }.show()
                return true
            }
            R.id.action_resetTrips -> {
                // prompt to delete all trips
                alert("Are you sure you want to delete all trips?") {
                    yesButton {
                        title = "Yes"
                        val resetTrips = TripDBHandler(this@PathsActivity, null, null, 1).resetTrips()
                        if (resetTrips) toast("Reset complete")
                            else toast("Oops, issue resetting")
                    }
                    noButton { toast("OK, nothing deleted") }
                }.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
