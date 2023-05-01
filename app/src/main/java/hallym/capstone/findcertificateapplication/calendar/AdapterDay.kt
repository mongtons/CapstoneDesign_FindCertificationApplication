package hallym.capstone.findcertificateapplication.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.ListItemDayBinding
import java.util.*


class ViewHolder(val binding : ListItemDayBinding):RecyclerView.ViewHolder(binding.root)

class AdapterDay(val tempMonth:Int, val dayList: MutableList<Date>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ROW = 6

    //inner class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  =
        ViewHolder(ListItemDayBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
//        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_day, parent, false)
//        return DayView(view)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ViewHolder).binding
        binding.itemDayLayout.setOnClickListener {
            Toast.makeText( binding.itemDayLayout.context, "${dayList[position]}", Toast.LENGTH_SHORT).show()
        }
//        holder.layout.item_day_layout.setOnClickListener {
//            Toast.makeText(holder.layout.context, "${dayList[position]}", Toast.LENGTH_SHORT).show()
//        }
        binding.itemDayText.text = dayList[position].date.toString()
        //holder.layout.item_day_text.text = dayList[position].date.toString()

        binding.itemDayText.setTextColor(when(position % 7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })
//        holder.layout.item_day_text.setTextColor(when(position % 7) {
//            0 -> Color.RED
//            6 -> Color.BLUE
//            else -> Color.BLACK
//        })

        if(tempMonth != dayList[position].month) {
            binding.itemDayText.alpha = 0.4f
//            holder.layout.item_day_text.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}