package com.example.assistedreminder
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ReminderViewAdapter internal constructor(
    context:Context
) : RecyclerView.Adapter<ReminderViewAdapter.ReminderViewHolder>(){

    private val inflater:LayoutInflater = LayoutInflater.from(context)

    private var reminders = emptyList<Reminder>()

    inner class ReminderViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val timeView: TextView = itemView.findViewById(R.id.timeView)
        val messageView: TextView = itemView.findViewById(R.id.messageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder{
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ReminderViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val current = reminders[position]


        if(current.time != null){
            val time = current.time
            val sdf = SimpleDateFormat( "HH:mm dd.MM.yyyy")
            sdf.timeZone = TimeZone.getDefault()
            holder.timeView.text = sdf.format(time)
        }

        holder.messageView.text = current.message
    }


//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
////        val row = inflater.inflate(R.layout.list_view_item, parent, false)
////        val reminder = getItem(position)
////        row.itemMessage.text =  reminder.message
////        row.itemTrigger.text = reminder.time.toString()
////        return row
////    }
////
////    override fun getItem(position: Int): Reminder{
////        return list[position]
////    }

    internal fun setReminders(reminders: List<Reminder>) {
        this.reminders= reminders
        notifyDataSetChanged()
    }

    override fun getItemCount() = reminders.size

//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    override fun getCount(): Int {
//        return list.size
//    }

}