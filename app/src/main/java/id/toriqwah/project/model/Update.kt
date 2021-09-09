package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Update (
    @SerializedName("id_order")
    var id_order: String = "",
    @SerializedName("id_shipment")
    var id_shipment: String = "",
    @SerializedName("id_driver")
    var id_driver: String = "",
    @SerializedName("id_status")
    var id_status: String = "",
    @SerializedName("received_by")
    var received_by: String? = null,
    @SerializedName("note")
    var note: String? = null,
    @SerializedName("reason_id")
    var reason_id: String? = null,

    @SerializedName("new_password")
    var new_pass: String? = null,
    @SerializedName("confirm_password")
    var confirm_pass: String? = null,
    @SerializedName("driver_id")
    var driver_id: String? = null


)