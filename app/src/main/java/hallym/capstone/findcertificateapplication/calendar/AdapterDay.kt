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
        ViewHolder(ListItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = (holder as ViewHolder).binding

        // 클릭 이벤트 리스너 설정
        binding.itemDayLayout.setOnClickListener {
            Toast.makeText(binding.itemDayLayout.context,
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
                            testDay1 = data.child("testDay")
                                .child("pass").child("note").child("2st")
                                .child("jun").child("day1").value.toString().toInt()
                            Log.d("testDay1","title의 일 : $testDay1")
                            break
                        }
                    }
                }
                if((tempMonth==certMonth) && (dayList[position].date.toString()==testDay1.toString())){
                    //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                    //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==testDay1.toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                    // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

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
                    //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                    //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
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