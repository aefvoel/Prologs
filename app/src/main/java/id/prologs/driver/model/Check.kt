package id.prologs.driver.model

import com.google.gson.annotations.SerializedName

data class Check (
        @SerializedName("driver_id")
        var driver_id: Int
)