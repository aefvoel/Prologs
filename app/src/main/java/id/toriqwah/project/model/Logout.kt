package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Logout (
        @SerializedName("attendance_id")
        var attendance_id: Int
)