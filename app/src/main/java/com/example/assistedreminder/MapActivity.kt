package com.example.assistedreminder

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assistedreminder.MainActivity.Companion.EXTRA_REPLY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.toast
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var gMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var selectedLocation: LatLng
    lateinit var selectedAddress: String
    lateinit var geofencingClient: GeofencingClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        geofencingClient = LocationServices.getGeofencingClient(this)
        (map_fragment as SupportMapFragment).getMapAsync(this)

        buttonSave.setOnClickListener {
            val message = editMessage.text
            if (message.isEmpty()) {
                toast("Please enter a message")
            } else if (selectedLocation == null) {
                toast("Please select a location")
            }

            val reminder = Reminder(
                uid = null,
                location = String.format(
                    "%.3f,%.3f",
                    selectedLocation.latitude,
                    selectedLocation.longitude
                ),
                address = selectedAddress,
                time = null,
                message = message.toString()
            )

            val replyIntent = Intent()
            replyIntent.putExtra(EXTRA_REPLY, reminder)
            setResult(Activity.RESULT_OK, replyIntent)


            finish()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (!grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                toast("The app needs all permissions to function properly! You will regret this!")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    toast("The app needs all permissions to function properly! You will regret this!")
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        gMap = map ?: return

        if (
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            || (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED)

        ) {
            gMap.isMyLocationEnabled = true

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    var latLong = LatLng(location.latitude, location.longitude)
                    with(gMap) {
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f))
                    }
                }
            }
        } else {
            var permission = mutableListOf<String>()
            permission.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            permission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permission.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(
                this,
                permission.toTypedArray(),
                123
            )
        }

        gMap.setOnMapClickListener { location: LatLng ->
            with(gMap) {
                clear()
                animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                var title = ""
                var city = ""
                try {
                    val addressList =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    city = addressList.get(0).locality
                    title = addressList.get(0).getAddressLine(0)
                } catch (e: Exception) {

                }
                val marker =
                    addMarker(MarkerOptions().position(location).snippet(city).title(title))
                marker.showInfoWindow()
                addCircle(
                    CircleOptions()
                        .center(location)
                        .strokeColor(Color.MAGENTA)
                        .fillColor(Color.CYAN)
                )
                selectedLocation = location
                selectedAddress = "$title, $city"
            }

        }
    }


}
