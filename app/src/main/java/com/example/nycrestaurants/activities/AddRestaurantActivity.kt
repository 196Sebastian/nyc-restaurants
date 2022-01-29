package com.example.nycrestaurants.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nycrestaurants.R
import com.example.nycrestaurants.database.DatabaseHandler
import com.example.nycrestaurants.models.NYCRestaurantModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddRestaurantActivity : AppCompatActivity(), View.OnClickListener{

    private var calendar = Calendar.getInstance()
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mNYCRestaurantDetails : NYCRestaurantModel? = null

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private val requestPermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        run {
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    Toast.makeText(
                        this@AddRestaurantActivity,
                        "Permission granted to read the storage files.",
                        Toast.LENGTH_LONG
                    ).show()

                    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this@AddRestaurantActivity,
                            "Oops you just denied the permission.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private var openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if(result.resultCode == RESULT_OK && result.data != null){
                val currentImageView = findViewById<ImageView>(R.id.iv_place_image)
                currentImageView.setImageURI(result.data?.data)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)
        setSupportActionBar(findViewById(R.id.toolbar_add_place))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Toolbar>(R.id.toolbar_add_place).setNavigationOnClickListener {
            onBackPressed()
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

        findViewById<Button>(R.id.btn_save).setOnClickListener{
            savingRestaurant()
        }

        val tvImage: TextView = findViewById(R.id.tv_add_image)
        tvImage.setOnClickListener {
            requestStoragePermission()
        }
    }

    private fun isReadStorageAllowed(): Boolean{
        var result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationaleDialog("NYC Restaurants", "Needs to Access your External Storage")
        }else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
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
                openGalleryLauncher
            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "MM.dd.yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())

        findViewById<EditText>(R.id.et_date).setText(simpleDateFormat.format(calendar.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun savingRestaurant(){

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
            false ->{
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }else -> {
            val nycRestaurantModel = NYCRestaurantModel(
                0,
                etTitle.text.toString(),
                openGalleryLauncher.toString(),
                etDescription.text.toString(),
                etDate.text.toString(),
                etLocation.text.toString(),
                mLatitude,
                mLongitude
            )
            val dbHandler = DatabaseHandler(this)
            val addRestaurant = dbHandler.addNycRestaurant(nycRestaurantModel)

            if (addRestaurant > 0) {
                setResult(Activity.RESULT_OK)
                finish()
            }
          }
        }
    }

    companion object {
        private const val GALLERY = 1
        private const val IMAGE_DIRECTORY = "NYCRestaurantImages"
    }
}