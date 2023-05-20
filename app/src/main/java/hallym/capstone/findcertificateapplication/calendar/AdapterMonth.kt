package hallym.capstone.findcertificateapplication.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import hallym.capstone.findcertificateapplication.databinding.ListItemMonthBinding
import java.util.*

class MonthViewHolder(val binding : ListItemMonthBinding):RecyclerView.ViewHolder(binding.root)

//var calendar = Calendar.getInstance()
class AdapterMonth() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    val center=Int.MAX_VALUE/2
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MonthViewHolder(ListItemMonthBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MonthViewHolder).binding
        calendar.time=Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, /*(position + currentMonth ) % 12)*/ position-center)
//        calendar.set(Calendar.YEAR, 2023)

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

        //Log.d("testMonth","testMonthList : " + testMonthList)
        val dayListManager = GridLayoutManager(binding.itemMonthDayList.context,7)

        val dayListAdapter = AdapterDay(tempMonth, dayList)
        //해당 자격증의 월은 월을 처리하는 AdapterMonth에서 따로 처리하여 testMonth라는 변수에 넣어 AdapterDay로 보냄

        binding.itemMonthDayList.apply{
            layoutManager = dayListManager
            adapter = dayListAdapter
        }
    }


    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}
