package id.prologs.driver.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import id.prologs.driver.R
import id.prologs.driver.model.Shipment
import id.prologs.driver.model.Shipper

class ShipmentAdapter(context: Context, list: ArrayList<Shipment>, data: Shipper, private val listener: Listener)
    : RecyclerView.Adapter<ShipmentAdapter.ShipmentViewHolder>() {

    private val contexts = context
    private val itemList = list
    private val itemData = data

    interface Listener{
        fun onReturnClicked(id: String, status: String)
        fun onDeliverClicked(id: String, status: String)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipmentViewHolder {
        val view = LayoutInflater.from(contexts).inflate(R.layout.item_detail, parent, false)
        return ShipmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ShipmentViewHolder, position: Int) {

        holder.consignee.text = itemList[position].consigneeName
        holder.address.text = itemList[position].consigneeAddress
        holder.item.text = itemList[position].notes

        holder.status.text = itemList[position].jobStatus

        when (itemList[position].jobStatusId) {
            "23" -> {
                holder.btnAction.text = "Deliver"
                holder.btnRetur.visibility = View.GONE
            }
            "24" -> {
                holder.btnAction.text = "Received"
                holder.btnRetur.visibility = View.VISIBLE
            }
            "25" -> {
                holder.btnAction.visibility = View.GONE
                holder.btnRetur.visibility = View.GONE
            }
            "26" -> {
                holder.btnAction.text = "Deliver"
                holder.btnRetur.visibility = View.GONE
            }
        }
        holder.btnMore.setOnClickListener {
            val option = arrayOf<CharSequence>(
                "Direction To Pickup Point",
                "Direction To Consignee",
                "Call Consignee"
            )
            val builder = AlertDialog.Builder(contexts)
            builder.setTitle(itemList[position].consigneeAddress)
            builder.setItems(option) { dialog, which ->
                when (which) {
                    0 -> {
                        val gmmPickupIntent = Uri.parse(
                            "https://www.google.com/maps/dir/?api=1" +
                                    "&destination=" + itemData.shipperLatitude + "," + itemData.shipperLongitude
                        )
                        val intent = Intent(Intent.ACTION_VIEW, gmmPickupIntent)
                        contexts.startActivity(intent)
                    }
                    1 -> {
                        val gmmIntent = Uri.parse(
                            "https://www.google.com/maps/dir/?api=1" +
                                    "&destination=" + itemList[position].consigneeLatitude + "," + itemList[position].consigneeLongitude
                        )
                        val intent1 = Intent(Intent.ACTION_VIEW, gmmIntent)
                        contexts.startActivity(intent1)
                    }
                    2 -> {
                        val intentCall = Intent(Intent.ACTION_DIAL)
                        intentCall.data = Uri.parse("tel:" + itemList[position].consigneePhone)
                        contexts.startActivity(intentCall)
                    }
                }
            }
            builder.show()
        }

        holder.btnRetur.setOnClickListener {
            listener.onReturnClicked(itemList[position].idShipment, itemList[position].jobStatusId)
        }

        holder.btnAction.setOnClickListener {
            listener.onDeliverClicked(itemList[position].idShipment, itemList[position].jobStatusId)
        }

    }


    inner class ShipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status: TextView = itemView.findViewById(R.id.status)
        val consignee: TextView = itemView.findViewById(R.id.consigne_name)
        val address: TextView = itemView.findViewById(R.id.address)
        val item: TextView = itemView.findViewById(R.id.item)
        val btnMore: ImageView = itemView.findViewById(R.id.btn_more)
        val btnAction: MaterialButton = itemView.findViewById(R.id.btn_action)
        val btnRetur: MaterialButton = itemView.findViewById(R.id.btn_retur)



    }
}