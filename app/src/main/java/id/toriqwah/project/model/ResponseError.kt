package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class ResponseError (
        @SerializedName("status")
        val status: Boolean,
        @SerializedName("msg")
        val message: String
)