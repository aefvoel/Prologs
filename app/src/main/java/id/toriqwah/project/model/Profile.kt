package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Profile (

        @SerializedName("id_driver") var idDriver : String,
        @SerializedName("id_vendor") var idVendor : String,
        @SerializedName("nik") var nik : String,
        @SerializedName("username") var username : String,
        @SerializedName("password") var password : String,
        @SerializedName("fullname") var fullname : String,
        @SerializedName("email") var email : String,
        @SerializedName("phone") var phone : String,
        @SerializedName("address") var address : String,
        @SerializedName("pic") var pic : String,
        @SerializedName("ktp") var ktp : String,
        @SerializedName("sim") var sim : String,
        @SerializedName("no_imei") var noImei : String,
        @SerializedName("no_ktp") var noKtp : String,
        @SerializedName("no_sim") var noSim : String,
        @SerializedName("expiry_ktp") var expiryKtp : String,
        @SerializedName("ktp_lifetime_validity") var ktpLifetimeValidity : String,
        @SerializedName("expiry_sim") var expirySim : String,
        @SerializedName("expiry_skck") var expirySkck : String,
        @SerializedName("token") var token : String,
        @SerializedName("poin") var poin : String,
        @SerializedName("description") var description : String,
        @SerializedName("status") var status : String,
        @SerializedName("created_at") var createdAt : String,
        @SerializedName("created_by") var createdBy : String,
        @SerializedName("updated_at") var updatedAt : String,
        @SerializedName("updated_by") var updatedBy : String,
        @SerializedName("checkedIn") var checkedIn : Boolean,
        @SerializedName("attendance_id") var attendanceId : String,
        @SerializedName("checkin_time") var checkinTime : Long,
        @SerializedName("radius") var radius : Int

)