package com.example.nycrestaurants.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nycrestaurants.R
import com.example.nycrestaurants.database.DatabaseHandler
import com.example.nycrestaurants.models.NYCRestaurantModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.fab_add_restaurant).setOnClickListener {
            val intent = Intent(this, AddRestaurantActivity::class.java)
            startActivity(intent)
        }

        getNYCRestaurantListFromLocalDB()
    }

    private fun getNYCRestaurantListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getNYCRestaurantList : ArrayList<NYCRestaurantModel> = dbHandler.getNYCRestaurantList()

        if(getNYCRestaurantList.size > 0){
            for(i in getNYCRestaurantList){
                Log.e("Title", i.title)
                Log.e("Description", i.description)
            }
        }
    }
}