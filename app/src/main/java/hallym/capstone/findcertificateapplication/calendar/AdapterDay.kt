package hallym.capstone.findcertificateapplication.calendar

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ListItemDayBinding
import java.util.*

val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
val database: FirebaseDatabase = FirebaseDatabase.getInstance()
val ref: DatabaseReference =database.getReference()
var reftitle = ArrayList<String>(3)
var reffavorite = ArrayList<String>(3)

class ViewHolder(val binding : ListItemDayBinding):RecyclerView.ViewHolder(binding.root)

@Suppress("DEPRECATION")
class AdapterDay(val tempMonth:Int, val dayList: MutableList<Date>, val certMonth:Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ROW = 6
    var testDay1=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            ListItemDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = (holder as ViewHolder).binding

        // 클릭 이벤트 리스너 설정
        binding.itemDayLayout.setOnClickListener {
            Toast.makeText(
                binding.itemDayLayout.context,
//                "${dayList[position]}",
                "${holder.adapterPosition}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Firebase 데이터베이스에서 자격증과 즐겨찾기 목록 데이터 가져오기
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 즐겨찾기 목록 가져오기
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

                // 자격증 목록 가져오기
                val certification = snapshot.child("Certification")
                for (i in reffavorite) {
                    for (data in certification.children) {
                        val title = data.child("title").value.toString()
                        if (title == i) {
                            Log.d("reftitle","즐겨찾기된 자격증에서의 title값 : $reftitle")
                            val testMonth = data.child("testDay")
                                .child("pass").child("note").child("2st")
                                .child("jun").child("month").value.toString().toInt()
                            testDay1 = data.child("testDay")
                                .child("pass").child("note").child("2st")
                                .child("jun").child("day1").value.toString().toInt()
                            Log.d("testMonth", "title의 월 : $testMonth")
                            Log.d("testDay1","title의 일 : $testDay1")
                            break
                        }
                    }
                }
                if((tempMonth==certMonth) && (dayList[position].date.toString()==testDay1.toString())){
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==testDay1.toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    Log.d("kim", positionList[cnt-1].toString())
                    if(testDay1<=10){
                        if(position==positionList[0])
                            binding.oneday.setBackgroundColor(Color.parseColor("#AAAAAA"))
                    }else if(testDay1>=20){
                        if(position==positionList[1])
                            binding.oneday.setBackgroundColor(Color.parseColor("#AAAAAA"))
                    }else{
                        binding.oneday.setBackgroundColor(Color.parseColor("#AAAAAA"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: Exception) {
                }
            }
        })
        binding.itemDayText.text = dayList[position].date.toString()
        binding.itemDayText.setTextColor(
            when (position % 7) {
                0 -> Color.RED
                6 -> Color.BLUE
                else -> Color.BLACK
            }
        )
        if (tempMonth != dayList[position].month) {
            binding.itemDayText.alpha = 0.4f
        } else {
            binding.itemDayText.alpha = 1.0f
        }
    }


    override fun getItemCount(): Int {
        return ROW * 7
    }
}