package com.example.nycrestaurants.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nycrestaurants.R
import com.example.nycrestaurants.adapters.NYCRestaurantAdapter
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

    private fun setUpNYCRestaurantRecyclerView(nycRestaurantList: ArrayList<NYCRestaurantModel>) {
        val rvList = findViewById<RecyclerView>(R.id.rv_nyc_restaurant_list)

        rvList.setHasFixedSize(true)

        rvList.layoutManager = LinearLayoutManager(this)
        val restaurantAdapter = NYCRestaurantAdapter(this, nycRestaurantList)
        rvList.adapter = restaurantAdapter
    }

    private fun getNYCRestaurantListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getNYCRestaurantList : ArrayList<NYCRestaurantModel> = dbHandler.getNYCRestaurantList()

        if(getNYCRestaurantList.size > 0){
            findViewById<RecyclerView>(R.id.rv_nyc_restaurant_list).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_no_records_available).visibility = View.GONE

            setUpNYCRestaurantRecyclerView(getNYCRestaurantList)
        }else{
            findViewById<RecyclerView>(R.id.rv_nyc_restaurant_list).visibility = View.GONE
            findViewById<TextView>(R.id.tv_no_records_available).visibility = View.VISIBLE
        }
    }
}