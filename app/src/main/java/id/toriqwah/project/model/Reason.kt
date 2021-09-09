package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Reason(
        @SerializedName("id") var id: String,
        @SerializedName("reason") var reason : String,
)