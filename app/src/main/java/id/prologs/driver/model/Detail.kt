package id.prologs.driver.model

import com.google.gson.annotations.SerializedName

data class Detail (
        @SerializedName("id_order")
        var id_order: Int,
        @SerializedName("source")
        var source: Int
)

data class DetailResponse (

        @SerializedName("status") var status : Boolean,
        @SerializedName("order") var order : Order,
        @SerializedName("shipper") var shipper : Shipper,
        @SerializedName("shipment") var shipment : ArrayList<Shipment>

)

data class Order (

        @SerializedName("id_order") var idOrder : String,
        @SerializedName("receipt_number") var receiptNumber : String,
        @SerializedName("is_multidrop") var isMultidrop : String,
        @SerializedName("total_distance") var totalDistance : String,
        @SerializedName("id_driver") var idDriver : String

)

data class Shipper (

        @SerializedName("id_shipper") var idShipper : String,
        @SerializedName("shipper_name") var shipperName : String,
        @SerializedName("shipper_address") var shipperAddress : String,
        @SerializedName("shipper_phone") var shipperPhone : String,
        @SerializedName("shipper_province") var shipperProvince : String,
        @SerializedName("shipper_city") var shipperCity : String,
        @SerializedName("shipper_subdistrict") var shipperSubdistrict : String,
        @SerializedName("shipper_village") var shipperVillage : String,
        @SerializedName("shipper_postcode") var shipperPostcode : String,
        @SerializedName("shipper_latitude") var shipperLatitude : String,
        @SerializedName("shipper_longitude") var shipperLongitude : String

)

data class Shipment (

        @SerializedName("id_shipment") var idShipment : String,
        @SerializedName("po_number") var poNumber : String,
        @SerializedName("do_number") var doNumber : String,
        @SerializedName("distance") var distance : String,
        @SerializedName("id_consignee") var idConsignee : String,
        @SerializedName("consignee_name") var consigneeName : String,
        @SerializedName("consignee_address") var consigneeAddress : String,
        @SerializedName("consignee_phone") var consigneePhone : String,
        @SerializedName("consignee_province") var consigneeProvince : String,
        @SerializedName("consignee_city") var consigneeCity : String,
        @SerializedName("consignee_subdistrict") var consigneeSubdistrict : String,
        @SerializedName("consignee_village") var consigneeVillage : String,
        @SerializedName("consignee_postcode") var consigneePostcode : String,
        @SerializedName("consignee_latitude") var consigneeLatitude : String,
        @SerializedName("consignee_longitude") var consigneeLongitude : String,
        @SerializedName("length") var length : String,
        @SerializedName("width") var width : String,
        @SerializedName("height") var height : String,
        @SerializedName("qty") var qty : String,
        @SerializedName("dimension") var dimension : String,
        @SerializedName("weight") var weight : String,
        @SerializedName("notes") var notes : String,
        @SerializedName("job_status_id") var jobStatusId : String,
        @SerializedName("job_status") var jobStatus : String

)