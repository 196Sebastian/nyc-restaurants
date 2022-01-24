package com.example.nycrestaurants

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*


class AddRestaurantActivity : AppCompatActivity(), View.OnClickListener{

    private var calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)
        setSupportActionBar(findViewById(R.id.toolbar_add_place))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<Toolbar>(R.id.toolbar_add_place).setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        findViewById<EditText>(R.id.et_date).setOnClickListener(this)
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
            R.id.tv_add_image ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")

                val pictureDialogItems = arrayOf("Select photo from Gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                        _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this@AddRestaurantActivity, "Camera selection coming soon...", Toast.LENGTH_LONG).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?){
                    if(report!!.areAllPermissionsGranted()){
                        Toast.makeText(this@AddRestaurantActivity,
                            "Storage READ/WRITE permission are granted. Now you can select an image from GALLERY", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required for this feature. " +
                "It can be enabled under the Applications Settings")
            .setPositiveButton("GO TO SETTINGS"){ _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView(){

        val myFormat = "MM.dd.yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())

        findViewById<EditText>(R.id.et_date).setText(simpleDateFormat.format(calendar.time).toString())

    }
}