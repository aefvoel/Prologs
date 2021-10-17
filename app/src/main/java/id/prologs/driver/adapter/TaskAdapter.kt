package id.prologs.driver.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import id.prologs.driver.R
import id.prologs.driver.model.Task

class TaskAdapter(context : Context, list: ArrayList<Task>, private val listener: Listener)
    : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val contexts = context
    private val itemList = list

    interface Listener{
        fun onItemClicked(data: Task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(contexts).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.receipt.text = itemList[position].receiptNumber
        holder.distance.text = itemList[position].totalDistance
        holder.shipper.text = itemList[position].shipperName

        if (itemList[position].isMultidrop == "0"){
            holder.type.text = "Single Drop"
            holder.type.backgroundTintList = contexts.getColorStateList(R.color.hard_grey)
        }
        else {
            holder.type.text = "Multi Drop"
            holder.type.backgroundTintList = contexts.getColorStateList(R.color.colorRed)
        }

        holder.status.text = itemList[position].jobStatus
        holder.call.setOnClickListener {
            val intentCall = Intent(Intent.ACTION_DIAL)
            intentCall.data = Uri.parse("tel:" + itemList[position].shipperPhone)
            contexts.startActivity(intentCall)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClicked(itemList[position])
        }

    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receipt: TextView = itemView.findViewById(R.id.receipt)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val shipper: TextView = itemView.findViewById(R.id.shipper)
        val type: TextView = itemView.findViewById(R.id.type)
        val status: TextView = itemView.findViewById(R.id.status)
        val call: ImageView = itemView.findViewById(R.id.call)


    }
}