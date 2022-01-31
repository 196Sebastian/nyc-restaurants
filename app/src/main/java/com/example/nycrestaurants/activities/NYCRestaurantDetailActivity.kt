package com.example.nycrestaurants.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import com.example.nycrestaurants.R
import com.example.nycrestaurants.models.NYCRestaurantModel

class NYCRestaurantDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nycrestaurant_detail)

        var nycRestaurantDetailModel : NYCRestaurantModel? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            nycRestaurantDetailModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as NYCRestaurantModel
        }

        if(nycRestaurantDetailModel != null){
            val tbDetails = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_place_detail)
            setSupportActionBar(tbDetails)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = nycRestaurantDetailModel.title

            tbDetails.setNavigationOnClickListener {
                onBackPressed()
            }

            findViewById<TextView>(R.id.tv_description).text = nycRestaurantDetailModel.description
            findViewById<TextView>(R.id.tv_location).text = nycRestaurantDetailModel.location

            findViewById<Button>(R.id.btn_view_on_map).setOnClickListener {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, nycRestaurantDetailModel)
                startActivity(intent)
            }

            findViewById<Button>(R.id.btn_share_location).setOnClickListener {
                val uri = "Check Out This NYC Restaurant!: ${nycRestaurantDetailModel.title}"
                var intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, uri)
                intent.type = "text/plain"
                intent = Intent.createChooser(intent, "Share Via:")
                startActivity(intent)
            }
        }
    }
}