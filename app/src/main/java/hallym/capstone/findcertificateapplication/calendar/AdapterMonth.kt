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
    val npassMonthList = mutableListOf<Int>()
    val ntestMonthList = mutableListOf<Int>()
    val nperiodMonthList = mutableListOf<Int>()
    val ppassMonthList = mutableListOf<Int>()
    val ptestMonthList = mutableListOf<Int>()
    val pperiodMonthList = mutableListOf<Int>()
    val testMonthList = mutableListOf<Int>()
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
                            val ntestMonth = data.child("testDay").child("test").child("note")
                            val ptestMonth = data.child("testDay").child("test").child("practice")
                            val nperiodMonth = data.child("testDay").child("period").child("note")
                            val pperiodMonth = data.child("testDay").child("period").child("practice")
                            val npassMonth = data.child("testDay").child("pass").child("note")
                            val ppassMonth = data.child("testDay").child("pass").child("practice")
                            testMonthList.add(npassMonth.child("1st").child("mar").child("month").value.toString().toInt()-1)//0
                            testMonthList.add(npassMonth.child("2st").child("jun").child("month").value.toString().toInt()-1)//1
                            testMonthList.add(npassMonth.child("4st").child("oct").child("month").value.toString().toInt()-1)//2

                            testMonthList.add(ntestMonth.child("1st").child("mar").child("month").value.toString().toInt()-1)//3
                            testMonthList.add(ntestMonth.child("2st").child("jun").child("month").value.toString().toInt()-1)//4
                            testMonthList.add(ntestMonth.child("4st").child("oct").child("month").value.toString().toInt()-1)//5
                            testMonthList.add(ntestMonth.child("4st").child("ste").child("month").value.toString().toInt()-1)//6

                            testMonthList.add(nperiodMonth.child("1st").child("feb").child("month").value.toString().toInt()-1)//7
                            testMonthList.add(nperiodMonth.child("2st").child("jun").child("month").value.toString().toInt()-1)//8
                            testMonthList.add(nperiodMonth.child("2st").child("may").child("month").value.toString().toInt()-1)//9
                            testMonthList.add(nperiodMonth.child("4st").child("ste").child("month").value.toString().toInt()-1)//10

                            if(ppassMonth.exists()){
                                testMonthList.add(ppassMonth.child("1st").child("may").child("month").value.toString().toInt()-1)//11
                                testMonthList.add(ppassMonth.child("2st").child("aug").child("month").value.toString().toInt()-1)//12
                                testMonthList.add(ppassMonth.child("4st").child("dec").child("month").value.toString().toInt()-1)//13

                                testMonthList.add(ptestMonth.child("1st").child("apr").child("month").value.toString().toInt()-1)//14
                                testMonthList.add(ptestMonth.child("1st").child("may").child("month").value.toString().toInt()-1)//15
                                testMonthList.add(ptestMonth.child("2st").child("aug").child("month").value.toString().toInt()-1)//16
                                testMonthList.add(ptestMonth.child("2st").child("jul").child("month").value.toString().toInt()-1)//17
                                testMonthList.add(ptestMonth.child("4st").child("dec").child("month").value.toString().toInt()-1)//18
                                testMonthList.add(ptestMonth.child("4st").child("nov").child("month").value.toString().toInt()-1)//19

                                testMonthList.add(pperiodMonth.child("1st").child("mar").child("month").value.toString().toInt()-1)//20
                                testMonthList.add(pperiodMonth.child("2st").child("jul").child("month").value.toString().toInt()-1)//21
                                testMonthList.add(pperiodMonth.child("4st").child("oct").child("month").value.toString().toInt()-1)//22
                                break
                            }
                            break
                        }
                    }
                }
                Log.d("testMonth","testMonthList : " + testMonthList)
                val dayListManager = GridLayoutManager(binding.itemMonthDayList.context,7)
                val dayListAdapter = AdapterDay(tempMonth, dayList, testMonthList)

                //해당 자격증의 월은 월을 처리하는 AdapterMonth에서 따로 처리하여 testMonth라는 변수에 넣어 AdapterDay로 보냄

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



