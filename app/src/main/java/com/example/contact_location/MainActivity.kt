package com.example.contact_location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    lateinit var listView  : ListView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        latitude=findViewById(R.id.latitude)
        longitude=findViewById(R.id.longitude)

        getCurrentLocation()

        checkPermission()

    }

/*    override fun onStart() {
        super.onStart()
        checkPermission
    }*/

    private fun getCurrentLocation(){



        if (checkPermissionsForLocation()){
            if(isLocationGranted()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location: Location?=task.result
                    if (location==null){
                        Toast.makeText(this, "System not working", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        longitude.text=location.longitude.toString()
                        latitude.text=location.latitude.toString()



                    }
                }

            }
            else{
                Toast.makeText(this, "Turn on Locationfrom setting", Toast.LENGTH_SHORT).show()
                val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else {

            requestPermissions()

        }
    }
    private fun isLocationGranted():Boolean{
        val locationManager: LocationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf( android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_REQUEST)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== PERMISSION_REQUEST){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "User granted the permission", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else
                Toast.makeText(this, "User denied the permission", Toast.LENGTH_SHORT).show()


        }
    }


    companion object{
        private const val PERMISSION_REQUEST=100
    }
    private fun checkPermissionsForLocation():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }



    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_CONTACTS)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(Manifest.permission.READ_CONTACTS)
        } else {
           read()
        }
    }


    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        when (it) {
            true -> { println("Permission has been granted by user")
                read()}
            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                //show your custom dialog and naviage to Permission seetings
            }
        }
    }

    fun read(){
        listView= findViewById<ListView>(R.id.lvcontact)
        val cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        startManagingCursor(cursor)
        val from= arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER)
        val to= intArrayOf(android.R.id.text1,android.R.id.text2)

        val simple: SimpleCursorAdapter =
            SimpleCursorAdapter(this,android.R.layout.simple_list_item_2,cursor,from, to)
        listView.adapter=simple

    }
}