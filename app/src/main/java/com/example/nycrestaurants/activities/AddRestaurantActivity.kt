package com.example.nycrestaurants.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nycrestaurants.R
import com.example.nycrestaurants.database.DatabaseHandler
import com.example.nycrestaurants.models.NYCRestaurantModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AddRestaurantActivity : AppCompatActivity(), View.OnClickListener{

    private var calendar = Calendar.getInstance()
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var saveImageToInternalStorage: Uri? = null
    private var mNYCRestaurantDetails : NYCRestaurantModel? = null

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    val launchGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> }


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

            saveImageToInternalStorage = Uri.parse(mNYCRestaurantDetails!!.image)

            findViewById<ImageView>(R.id.iv_place_image).setImageURI(saveImageToInternalStorage)
            findViewById<Button>(R.id.btn_save).text = "UPDATE"
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener(this)
        findViewById<EditText>(R.id.et_location).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_add_image).setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(this@AddRestaurantActivity, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.iv_place_image ->{
                val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this, "Functionality coming soon!", Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDialog.show()
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
                        saveImageToInternalStorage.toString(),
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                        findViewById<ImageView>(R.id.iv_place_image)!!.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddRestaurantActivity, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)
                Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                findViewById<ImageView>(R.id.iv_place_image)!!.setImageBitmap(thumbnail)
            }
            else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                val place: Place = Autocomplete.getPlaceFromIntent(data!!)

                findViewById<EditText>(R.id.et_location).setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {

                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )

                            startActivityForResult(galleryIntent, GALLERY)
                        }
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "NYCRestaurantImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
}