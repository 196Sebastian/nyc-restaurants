package com.example.nycrestaurants.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.nycrestaurants.models.NYCRestaurantModel

class DatabaseHandler (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database version
        private  const val  DATABASE_VERSION = 1
        // Database Name
        private const val DATABASE_NAME = "NYCRestaurantDatabase"
        // Table Name
        private const val TABLE_NYC_RESTAURANT = "NYCRestaurantTable"

        //All the Columns Names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Creating table with fields
        val CREATE_NYC_RESTAURANT_TABLE = ("CREATE TABLE " + TABLE_NYC_RESTAURANT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_NYC_RESTAURANT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NYC_RESTAURANT")
        onCreate(db)
    }

    fun addNycRestaurant(nycRestaurant: NYCRestaurantModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, nycRestaurant.title)
        contentValues.put(KEY_DESCRIPTION, nycRestaurant.description)
        contentValues.put(KEY_DATE, nycRestaurant.date)
        contentValues.put(KEY_LOCATION, nycRestaurant.location)
        contentValues.put(KEY_LATITUDE, nycRestaurant.latitude)
        contentValues.put(KEY_LONGITUDE, nycRestaurant.longitude)

        val result = db.insert(TABLE_NYC_RESTAURANT, null, contentValues)

        db.close()
        return result
    }

    fun updateNycRestaurant(nycRestaurant: NYCRestaurantModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, nycRestaurant.title)
        contentValues.put(KEY_DESCRIPTION, nycRestaurant.description)
        contentValues.put(KEY_DATE, nycRestaurant.date)
        contentValues.put(KEY_LOCATION, nycRestaurant.location)
        contentValues.put(KEY_LATITUDE, nycRestaurant.latitude)
        contentValues.put(KEY_LONGITUDE, nycRestaurant.longitude)

        val success = db.update(TABLE_NYC_RESTAURANT, contentValues, KEY_ID + "=" +nycRestaurant.id, null)

        db.close()
        return success
    }

    fun deleteNYCRestaurant(nycRestaurant: NYCRestaurantModel): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NYC_RESTAURANT, KEY_ID + "=" + nycRestaurant.id, null)
        db.close()

        return success
    }

   //@SuppressLint("Range")
    fun getNYCRestaurantList(): ArrayList<NYCRestaurantModel>{

        val nycRestaurantList = ArrayList<NYCRestaurantModel>()
        val selectQuery = "SELECT * FROM $TABLE_NYC_RESTAURANT"
        val db = this.readableDatabase

        try{
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if(cursor.moveToFirst()){
                do {
                    val place = NYCRestaurantModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        )
                    nycRestaurantList.add(place)

                }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        return nycRestaurantList
    }
}