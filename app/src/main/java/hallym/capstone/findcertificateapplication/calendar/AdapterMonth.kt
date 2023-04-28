package hallym.capstone.findcertificateapplication.calendar

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hallym.capstone.findcertificateapplication.databinding.ListItemMonthBinding
import java.util.*


class MonthViewHolder(val binding : ListItemMonthBinding):RecyclerView.ViewHolder(binding.root)

//var calendar = Calendar.getInstance()


class AdapterMonth() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val center = Int.MAX_VALUE / 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MonthViewHolder(ListItemMonthBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2023)
        calendar.set(Calendar.MONTH, position)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val binding = (holder as MonthViewHolder).binding
        binding.itemMonthText.text = String.format("%d년 %d월", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

        val tempMonth = calendar.get(Calendar.MONTH)

        var dayList: MutableList<Date> = MutableList(6 * 7) { Date() }
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        val dayListManager = GridLayoutManager(binding.itemMonthDayList.context,7)
        val dayListAdapter = AdapterDay(tempMonth, dayList)

        binding.itemMonthDayList.apply{
            layoutManager = dayListManager
            adapter = dayListAdapter
        }
    }

    override fun getItemCount(): Int {
        return 20 // 1년에 12개의 월이 있으므로, 아이템 개수를 12로 고정
    }
}
