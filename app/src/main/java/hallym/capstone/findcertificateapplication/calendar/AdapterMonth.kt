package hallym.capstone.findcertificateapplication.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
    var testMonth=0
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MonthViewHolder(ListItemMonthBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MonthViewHolder).binding
        calendar.time=Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, /*(position + currentMonth ) % 12)*/ position-center)
        calendar.set(Calendar.YEAR, 2023)

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
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorite = snapshot.child("Favorite")
                val favoriteChild = mFirebaseAuth.currentUser?.uid?.let { favorite.child(it) }
                if (favoriteChild != null) {
                    for (ds in favoriteChild.children) {
                        // 로그인한 사용자와 동일한 uid인지 확인
                        if (ds.child("uid").value.toString() == mFirebaseAuth.currentUser?.uid) {
                            val cerTitle = ds.child("cerTitle").value.toString()
                            if (!reffavorite.contains(cerTitle)) {
                                reffavorite.add(cerTitle)
                                Log.d("reffavorite","즐겨찾기된 자격증 : $reffavorite")
                            }
                        }
                    }
                }
                val certification = snapshot.child("Certification")
                for (i in reffavorite) {
                    for (data in certification.children) {
                        val title = data.child("title").value.toString()
                        if (title == i) {
                            Log.d("reftitle","즐겨찾기된 자격증에서의 title값 : $reftitle")
                            testMonth = data.child("testDay")
                                .child("pass").child("note").child("2st")
                                .child("jun").child("month").value.toString().toInt()
                            Log.d("testMonth", "title의 월 : $testMonth")
                            break
                        }
                    }
                }
                val dayListManager = GridLayoutManager(binding.itemMonthDayList.context,7)
                val dayListAdapter = AdapterDay(tempMonth, dayList, (testMonth-1))

                binding.itemMonthDayList.apply{
                    layoutManager = dayListManager
                    adapter = dayListAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: Exception) {
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}