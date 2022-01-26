package com.example.nycrestaurants.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
        contentValues.put(KEY_IMAGE, nycRestaurant.image)
        contentValues.put(KEY_DESCRIPTION, nycRestaurant.description)
        contentValues.put(KEY_DATE, nycRestaurant.date)
        contentValues.put(KEY_LOCATION, nycRestaurant.location)
        contentValues.put(KEY_LATITUDE, nycRestaurant.latitude)
        contentValues.put(KEY_LONGITUDE, nycRestaurant.longitude)

        val result = db.insert(TABLE_NYC_RESTAURANT, null, contentValues)

        db.close()
        return result

    }
}