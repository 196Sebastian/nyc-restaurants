package com.example.nycrestaurants.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nycrestaurants.R
import com.example.nycrestaurants.adapters.NYCRestaurantAdapter
import com.example.nycrestaurants.database.DatabaseHandler
import com.example.nycrestaurants.models.NYCRestaurantModel
import com.example.nycrestaurants.utils.SwipeToDelete
import com.example.nycrestaurants.utils.SwipeToEditCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.fab_add_restaurant).setOnClickListener {
            val intent = Intent(this, AddRestaurantActivity::class.java)
            startActivityForResult(intent, ADD_NYC_ACTIVITY_REQUEST_CODE)
        }

        getNYCRestaurantListFromLocalDB()
    }

    private fun setUpNYCRestaurantRecyclerView(nycRestaurantList: ArrayList<NYCRestaurantModel>) {
        val rvList = findViewById<RecyclerView>(R.id.rv_nyc_restaurant_list)

        rvList.setHasFixedSize(true)

        rvList.layoutManager = LinearLayoutManager(this)
        val restaurantAdapter = NYCRestaurantAdapter(this, nycRestaurantList)
        rvList.adapter = restaurantAdapter

        restaurantAdapter.setOnClickListener(object : NYCRestaurantAdapter.OnClickListener{
            override fun onClick(position: Int, model: NYCRestaurantModel) {
                val intent = Intent(this@MainActivity, NYCRestaurantDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rvAddList: RecyclerView = findViewById(R.id.rv_nyc_restaurant_list)
                val adapter = rvAddList.adapter as NYCRestaurantAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_NYC_ACTIVITY_REQUEST_CODE)

            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(findViewById(R.id.rv_nyc_restaurant_list))

        val deleteSwipeHandler = object : SwipeToDelete(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val rvAddList: RecyclerView = findViewById(R.id.rv_nyc_restaurant_list)
                val adapter = rvAddList.adapter as NYCRestaurantAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getNYCRestaurantListFromLocalDB()
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(findViewById(R.id.rv_nyc_restaurant_list))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_NYC_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getNYCRestaurantListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or Back pressed")
            }
        }
    }

    companion object {
        var ADD_NYC_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}