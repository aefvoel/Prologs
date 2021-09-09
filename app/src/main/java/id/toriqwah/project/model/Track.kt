package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Track (
        @SerializedName("id_driver")
        var id_driver: String,
        @SerializedName("latitude")
        var latitude: String,
        @SerializedName("longitude")
        var longitude: String,
        @SerializedName("battery_status")
        var battery_status: String,
        @SerializedName("battery_level")
        var battery_level: String,
        @SerializedName("gps_status")
        var gps_status: String
)