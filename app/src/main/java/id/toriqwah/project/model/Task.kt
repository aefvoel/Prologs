package id.toriqwah.project.model

import com.google.gson.annotations.SerializedName

data class Task (

        @SerializedName("id_order") var idOrder : String,
        @SerializedName("schedule_date") var scheduleDate : String,
        @SerializedName("receipt_number") var receiptNumber : String,
        @SerializedName("total_distance") var totalDistance : String,
        @SerializedName("job_status_id") var jobStatusId : String,
        @SerializedName("id_status") var idStatus : String,
        @SerializedName("job_status") var jobStatus : String,
        @SerializedName("is_multidrop") var isMultidrop : String,
        @SerializedName("id_shipper") var idShipper : String,
        @SerializedName("shipper_name") var shipperName : String,
        @SerializedName("shipper_address") var shipperAddress : String,
        @SerializedName("shipper_phone") var shipperPhone : String

)