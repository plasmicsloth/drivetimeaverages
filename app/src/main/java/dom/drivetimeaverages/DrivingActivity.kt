package dom.drivetimeaverages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_driving.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton


class DrivingActivity : AppCompatActivity() {

    // initialize class wide variables
    var MillisecondTime: Long = 0
    var StartTime: Long = 0
    var TimeBuff:Long = 0
    var UpdateTime = 0L
    var Seconds: Int = 0
    var Minutes:Int = 0
    var currentLights:Int = 0
    var displayTime: String = "00:00:00"
    var whichPath: String? = null
    val pathHandler = PathDBHandler(this, null, null, 1)
    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driving)

        txtTime.text = displayTime

        //populate radio buttons with list of paths
        val paths = pathHandler.findPath("all")

        if (paths!!.count() > 0) {
            radioPath.removeAllViews()
            for (path in paths) {
                val newRadio = RadioButton(this)
                with (newRadio) {
                    id = path.id
                    text = path.pathName
                    textSize = 20F
                    isChecked = false
                    setOnClickListener { (updateLights(path.pathName) ) }
                }
                radioPath.addView(newRadio)
            }
        }
        // start the stopwatch
        StartTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 100)
    }

    fun lightsPlus(v: View) {
        currentLights = Integer.parseInt(txtLights.text.toString())
        currentLights++
        txtLights.text = currentLights.toString()
    }
    fun lightsMinus(v: View) {
        currentLights = Integer.parseInt(txtLights.text.toString())
        if (currentLights <= 0) {
            currentLights = 0
        } else {
            currentLights--
        }
        txtLights.text = currentLights.toString()
    }

    private fun updateLights(pathName: String?) {
        //val selectedPath = pathHandler.findPath(pathName!!)
        //whichPath = selectedPath.first().pathName
        whichPath = pathName
    }

    fun finishDrive(v: View) {
        // save current time and stop the stopwatch
        TimeBuff += MillisecondTime
        handler.removeCallbacks(runnable)

        // prompt for path selection if nothing has been selected
        if (whichPath.isNullOrEmpty()) {
            alert {
                title = "Please select a path"
                okButton { radioPath.requestFocus() }
            }.show()
            return
        } else {
            // save time & ligths to database
            val tripHandler = TripDBHandler(this, null, null, 1)
            val lights = Integer.parseInt(txtLights.text.toString())
            val trip = Trip(whichPath!!, lights, TimeBuff.toInt())
            tripHandler.addTrip(trip)
            pathHandler.close()
            tripHandler.close()
            // run finish routine
            finish()
        }
    }
    override fun finish() {
        // pass along results to main activity
        val result = Intent()
        result.putExtra("displaytime", displayTime)
        result.putExtra("rawtime", TimeBuff)
        // back to main
        setResult(Activity.RESULT_OK, result)
        super.finish()
    }

    var runnable: Runnable = object : Runnable {
        override fun run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime
            UpdateTime = TimeBuff + MillisecondTime
            Seconds = (UpdateTime / 1000).toInt()
            Minutes = Seconds / 60
            Seconds = Seconds % 60
            displayTime = ("$Minutes:" + String.format("%02d", Seconds))
            txtTime.text = displayTime
            handler.postDelayed(this, 100)
        }
    }

}
