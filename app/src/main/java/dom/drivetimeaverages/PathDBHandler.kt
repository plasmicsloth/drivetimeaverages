package dom.drivetimeaverages

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PathDBHandler(context: Context, name: String?,
                    factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PATH_TABLE = ("CREATE TABLE $TABLE_PATHS ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, $COLUMN_LIGHTS INTEGER)")
        db.execSQL(CREATE_PATH_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PATHS")
        onCreate(db)
    }

    fun addPath(path: Path) {
        // set values of newly created path
        val values = ContentValues()
        values.put(COLUMN_NAME, path.pathName)
        values.put(COLUMN_LIGHTS, path.possibleLights)
        // and write to database
        val db = this.writableDatabase
        db.insert(TABLE_PATHS, null, values)
        db.close()
    }

    fun findPath(pathName: String) : List<Path> {
        var path: Path? = null
        var paths: MutableList<Path> = ArrayList()
        var query = "SELECT * FROM $TABLE_PATHS"
        if (pathName != "all") query += " WHERE $COLUMN_NAME LIKE '%$pathName'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                // store all items into an array list
                val id = Integer.parseInt(cursor.getString(0))
                val name = cursor.getString(1)
                val lights = Integer.parseInt(cursor.getString(2))
                path = Path(id, name, lights)
                paths.add(path)
            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()
        return paths
    }

    fun deletePath(pathName: String): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_PATHS WHERE $COLUMN_NAME = '$pathName'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_PATHS, COLUMN_ID + " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    fun resetPaths(): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_PATHS"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(0))
                db.delete(TABLE_PATHS, COLUMN_ID + " = ?", arrayOf(id.toString()))
            } while (cursor.moveToNext())
            result = true
        } else if (cursor.count == 0) return true
        cursor.close()
        db.close()
        return result
    }

    companion object {
        private val DATABASE_VERSION = 3
        private val DATABASE_NAME = "pathDB.db"
        val TABLE_PATHS = "paths"

        val COLUMN_ID = "_id"
        val COLUMN_NAME = "pathName"
        val COLUMN_LIGHTS = "possibleLightgs"
    }
}