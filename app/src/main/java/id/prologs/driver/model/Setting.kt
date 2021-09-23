package id.prologs.driver.model

import com.google.gson.annotations.SerializedName

data class Setting (

    @SerializedName("logo") var logo : String,
    @SerializedName("splash_screen") var splashScreen : String,
    @SerializedName("login_screen") var loginScreen : String,
    @SerializedName("tagline") var tagline : String,
    @SerializedName("track_interval") var trackInterval : String

)