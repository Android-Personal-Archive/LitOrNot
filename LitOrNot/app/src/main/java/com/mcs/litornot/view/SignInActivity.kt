package com.mcs.litornot.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.mcs.litornot.R
import kotlinx.android.synthetic.main.activity_sign_in.*

private lateinit var LL: String
class SignInActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var PERMISSION_ID = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        btn_login.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("LatLng", LL)
            startActivity(intent)
        }
    }

    // get last location
    private fun getLastLocation(){

        //check permission
        if(checkPermission()){
            //check location service enabled on device
            if(isLocationEnabled()){
                //get location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{
                    /*
                    var location: Location? = it.result
                    if(location == null){
                        fusedLocationProviderClient.requestLocationUpdates(getNewLocation(), locationCallback, Looper.myLooper())

                    }else{
                        LL = "${location.latitude}, ${location.longitude}"
                        Toast.makeText(this, LL, Toast.LENGTH_LONG).show()
                    }
                    */
                    fusedLocationProviderClient.requestLocationUpdates(getNewLocation(), locationCallback, Looper.myLooper())
                }
            }else{
                Toast.makeText(this, "Please enable your location service", Toast.LENGTH_LONG).show()
            }
        }else{
            requestPermission()
        }
    }

    private fun getNewLocation(): LocationRequest{
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        return locationRequest
    }

    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location = p0.lastLocation

            // set new location
            LL = "${lastLocation.latitude}, ${lastLocation.longitude}"
        }
    }

    // check uses permissions
    private fun checkPermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true

        return false
    }

    //request user permission
    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
    }

    // check location service enabled on device
    private fun isLocationEnabled(): Boolean{
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //built in function that checks permission result
        //using it only for debugging purposes
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:", "You have the permission")
            }
        }
    }
}