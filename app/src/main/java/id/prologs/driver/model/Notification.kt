package id.prologs.driver.model

import com.google.gson.annotations.SerializedName

data class Notification (

    @SerializedName("id_notification") var idNotification : String,
    @SerializedName("id_order") var idOrder : String,
    @SerializedName("id_driver") var idDriver : String,
    @SerializedName("short_message") var shortMessage : String,
    @SerializedName("detail_message") var detailMessage : String,
    @SerializedName("mark_as_read") var markAsRead : String,
    @SerializedName("created_by") var createdBy : String,
    @SerializedName("created_at") var createdAt : String

)