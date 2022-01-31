package com.example.nycrestaurants.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.nycrestaurants.R
import com.example.nycrestaurants.models.NYCRestaurantModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mNYCRestaurantDetails: NYCRestaurantModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mNYCRestaurantDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as NYCRestaurantModel
        }

        if(mNYCRestaurantDetails != null){
            setSupportActionBar(findViewById(R.id.toolbar_map))
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mNYCRestaurantDetails!!.title

            findViewById<Toolbar>(R.id.toolbar_map).setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(mNYCRestaurantDetails!!.latitude, mNYCRestaurantDetails!!.longitude)
        googleMap!!.addMarker(MarkerOptions().position(position).title(mNYCRestaurantDetails!!.location))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)

    }
}