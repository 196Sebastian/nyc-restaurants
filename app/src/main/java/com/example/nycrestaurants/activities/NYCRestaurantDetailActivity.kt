package com.example.nycrestaurants.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

            findViewById<ImageView>(R.id.iv_place_image).setImageURI(Uri.parse(nycRestaurantDetailModel.image))
            findViewById<TextView>(R.id.tv_description).text = nycRestaurantDetailModel.description
            findViewById<TextView>(R.id.tv_location).text = nycRestaurantDetailModel.location
        }
    }
}