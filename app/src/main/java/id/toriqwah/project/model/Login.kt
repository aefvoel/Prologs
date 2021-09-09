package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Login (
        @SerializedName("imei")
        var imei: String,
        @SerializedName("token")
        var token: String,
        @SerializedName("password")
        var password: String
)