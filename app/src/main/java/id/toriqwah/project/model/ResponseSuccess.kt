package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class ResponseSuccess<T> (
        @SerializedName("status") val status: Boolean,
        @SerializedName("info") var info : Info,
        @SerializedName("attendance_id") val id: Int,
        @SerializedName("error") var error : Error,
        @SerializedName("message") var message : String,
        @SerializedName("data") val data: T
)

data class Info (

        @SerializedName("title") var title : String,
        @SerializedName("message") var message : String

)

data class Error (

        @SerializedName("code") var code : Int,
        @SerializedName("message") var message : String

)