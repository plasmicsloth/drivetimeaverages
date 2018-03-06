package dom.drivetimeaverages

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TripDBHandler(context: Context, time: Int?,
        factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TRIP_TABLE = ("CREATE TABLE $TABLE_TRIPS ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_PATH STRING, $COLUMN_LIGHTS INTEGER, $COLUMN_TIME INTEGER)")
        db.execSQL(CREATE_TRIP_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRIPS")
        onCreate(db)
    }

    fun addTrip(trip: Trip) {
        // set values of newly created path
        val values = ContentValues()
        values.put(COLUMN_PATH, trip.path)
        values.put(COLUMN_TIME, trip.milliseconds)
        values.put(COLUMN_LIGHTS, trip.tripLights)
        // and write to database
        val db = this.writableDatabase
        db.insert(TABLE_TRIPS, null, values)
        db.close()
    }

    fun findTrips(pathName: String): List<Trip>? {
        var trip: Trip? = null
        var trips: MutableList<Trip> = ArrayList()
        var query = ""
        if (pathName == "all") query = "SELECT * FROM $TABLE_TRIPS"
            else query = "SELECT * FROM $TABLE_TRIPS WHERE $COLUMN_PATH = '$pathName'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                // store all items into an array list
                val id = Integer.parseInt(cursor.getString(0))
                val path = cursor.getString(1)
                val time = Integer.parseInt(cursor.getString(2))
                val lights = Integer.parseInt(cursor.getString(3))
                trip = Trip(id, path, time, lights)
                trips.add(trip)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return trips
    }

    fun deleteTrips(pathName: String): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_TRIPS WHERE $COLUMN_PATH = '$pathName'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(0))
                db.delete(TABLE_TRIPS, COLUMN_ID + " = ?", arrayOf(id.toString()))
            } while (cursor.moveToNext())
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    fun resetTrips(): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_TRIPS"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(0))
                db.delete(TABLE_TRIPS, COLUMN_ID + " = ?", arrayOf(id.toString()))
            } while (cursor.moveToNext())
            result = true
        } else if (cursor.count == 0) return true
        cursor.close()
        db.close()
        return result
    }

    companion object {
        private val DATABASE_VERSION = 3
        private val DATABASE_NAME = "tripDB.db"
        val TABLE_TRIPS = "trips"

        val COLUMN_ID = "_id"
        val COLUMN_PATH = "path"
        val COLUMN_LIGHTS = "lights"
        val COLUMN_TIME = "milliseconds"
    }
}
