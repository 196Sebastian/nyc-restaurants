package com.example.nycrestaurants.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.nycrestaurants.R
import com.example.nycrestaurants.database.DatabaseHandler
import com.example.nycrestaurants.models.NYCRestaurantModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AddRestaurantActivity : AppCompatActivity(), View.OnClickListener{

    private var calendar = Calendar.getInstance()
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mNYCRestaurantDetails : NYCRestaurantModel? = null

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)

        setSupportActionBar(findViewById(R.id.toolbar_add_place))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Toolbar>(R.id.toolbar_add_place).setNavigationOnClickListener {
            onBackPressed()
        }

        if(!Places.isInitialized()){
             Places.initialize(this@AddRestaurantActivity, resources.getString(R.string.google_maps_api_key))
        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mNYCRestaurantDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as NYCRestaurantModel
        }

        findViewById<EditText>(R.id.et_date).setOnClickListener(this)
        dateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()

        if(mNYCRestaurantDetails != null){
            supportActionBar?.title = "Edit NYC Restaurant"
            findViewById<EditText>(R.id.et_title).setText(mNYCRestaurantDetails!!.title)
            findViewById<EditText>(R.id.et_description).setText(mNYCRestaurantDetails!!.description)
            findViewById<EditText>(R.id.et_date).setText(mNYCRestaurantDetails!!.date)
            findViewById<EditText>(R.id.et_location).setText(mNYCRestaurantDetails!!.location)
            mLatitude = mNYCRestaurantDetails!!.latitude
            mLongitude = mNYCRestaurantDetails!!.longitude

            findViewById<Button>(R.id.btn_save).text = "UPDATE"
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener(this)
        findViewById<EditText>(R.id.et_location).setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(this@AddRestaurantActivity, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.btn_save -> {
                val etTitle: EditText = findViewById(R.id.et_title)
                val etDescription: EditText = findViewById(R.id.et_description)
                val etLocation: EditText = findViewById(R.id.et_location)
                val etDate: EditText = findViewById(R.id.et_date)

                when{
                    etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
                    }
                    etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
                    }
                   else -> {
                    val nycRestaurantModel = NYCRestaurantModel(
                        if(mNYCRestaurantDetails == null) 0 else mNYCRestaurantDetails!!.id,
                        etTitle.text.toString(),
                        etDescription.text.toString(),
                        etDate.text.toString(),
                        etLocation.text.toString(),
                        mLatitude,
                        mLongitude
                    )
                    val dbHandler = DatabaseHandler(this)

                    if(mNYCRestaurantDetails == null){
                        val addRestaurant = dbHandler.addNycRestaurant(nycRestaurantModel)

                        if (addRestaurant > 0) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    } else {

                        val updateNYCRestaurant = dbHandler.updateNycRestaurant(nycRestaurantModel)

                        if(updateNYCRestaurant > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }
                }
            }
            R.id.et_location ->{
                try {
                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this@AddRestaurantActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "MM.dd.yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())

        findViewById<EditText>(R.id.et_date).setText(simpleDateFormat.format(calendar.time).toString())
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                val place: Place = Autocomplete.getPlaceFromIntent(data!!)

                findViewById<EditText>(R.id.et_location).setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude

        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    companion object {
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
}