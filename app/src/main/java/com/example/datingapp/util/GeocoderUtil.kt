package com.example.datingapp.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import java.util.Locale

object GeocoderUtil {
    @Suppress("DEPRECATION")
    fun geocoding(context: Context, lat: Double, lng: Double) : String {
        var city: String? = null
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            geocoder.getFromLocation(lat, lng, 1
            ) { address ->
                city =
                    if (address[0].locality == null) address[0].subLocality else address[0].locality
            }
        } else {

            val address = geocoder.getFromLocation(lat, lng, 1)
            try {
                if (address != null) {
                    if (address.isNotEmpty()) {
                        city = if (address[0].locality == null) address[0].subLocality else address[0].locality
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return city ?: "NOT_FOUND_LOCATION"
    }
}