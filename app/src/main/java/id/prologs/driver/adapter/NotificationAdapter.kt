package id.prologs.driver.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.prologs.driver.R
import id.prologs.driver.model.Notification

class NotificationAdapter(context : Context, list: ArrayList<Notification>, private val listener: Listener)
    : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val contexts = context
    private val itemList = list

    interface Listener{
        fun onItemClicked(data: Notification)
        fun onRemove(data: Notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(contexts).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        holder.title.text = itemList[position].shortMessage

        if (itemList[position].markAsRead == "0"){
            holder.title.typeface = Typeface.DEFAULT_BOLD
        }
        else {
            holder.title.typeface = Typeface.DEFAULT
        }

        holder.itemView.setOnClickListener {
            listener.onItemClicked(itemList[position])
        }
        holder.delete.setOnClickListener {
            listener.onRemove(itemList[position])
        }

    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val delete: ImageView = itemView.findViewById(R.id.delete)


    }
}