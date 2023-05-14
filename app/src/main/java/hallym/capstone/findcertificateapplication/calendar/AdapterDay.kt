package hallym.capstone.findcertificateapplication.calendar

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.integrity.internal.j
import com.google.android.play.integrity.internal.l
import com.google.android.play.integrity.internal.w
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ListItemDayBinding
import hallym.capstone.findcertificateapplication.datatype.testmonth
import java.util.*

val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
val database: FirebaseDatabase = FirebaseDatabase.getInstance()
val ref: DatabaseReference =database.getReference()
var reftitle = ArrayList<String>(3)
var reffavorite = ArrayList<String>(3)

class ViewHolder(val binding : ListItemDayBinding):RecyclerView.ViewHolder(binding.root)

@Suppress("DEPRECATION")
class AdapterDay(val tempMonth:Int, val dayList: MutableList<Date>, val certMonth:MutableList<Int>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ROW = 6
    val npassDayList = mutableListOf<Int>()
    val n1testDayList = mutableListOf<Int>()
    val n2testDayList = mutableListOf<Int>()
    val n3testDayList = mutableListOf<Int>()
    val n4testDayList = mutableListOf<Int>()
    //val ntestDayList = mutableListOf<Int>()
    val nperiodDayList = mutableListOf<Int>()
    val n1periodDayList = mutableListOf<Int>()
    val n2periodDayList = mutableListOf<Int>()
    val n3periodDayList = mutableListOf<Int>()

    val p1testDayList = mutableListOf<Int>()
    val p2testDayList = mutableListOf<Int>()
    val p3testDayList = mutableListOf<Int>()
    val p4testDayList = mutableListOf<Int>()
    val p5testDayList = mutableListOf<Int>()
    val p6testDayList = mutableListOf<Int>()

    val p1periodDayList = mutableListOf<Int>()
    val p2periodDayList = mutableListOf<Int>()
    val p3periodDayList = mutableListOf<Int>()
    val ppassDayList = mutableListOf<Int>()
    val ptestDayList = mutableListOf<Int>()
    val pperiodDayList = mutableListOf<Int>()


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
                            val npassDay = data.child("testDay").child("pass").child("note")
                            val ppassDay = data.child("testDay").child("pass").child("practice")
                            val ntestDay = data.child("testDay").child("test").child("note")
                            val ptestDay = data.child("testDay").child("test").child("practice")
                            val nperiodDay = data.child("testDay").child("period").child("note")
                            val pperiodDay = data.child("testDay").child("period").child("practice")

                            npassDayList.add(npassDay.child("1st").child("mar").child("day1").value.toString().toInt())
                            npassDayList.add(npassDay.child("2st").child("jun").child("day1").value.toString().toInt())
                            npassDayList.add(npassDay.child("4st").child("oct").child("day1").value.toString().toInt())
//
                            for(i in 1..10){
                                n1testDayList.add(ntestDay.child("1st").child("mar").child("day$i").value.toString().toInt())
                            }
                            for(i in 1..9){
                                n2testDayList.add(ntestDay.child("2st").child("jun").child("day$i").value.toString().toInt())
                            }
                            for(i in 1..18){
                                n3testDayList.add(ntestDay.child("4st").child("oct").child("day$i").value.toString().toInt())
                            }
                            for(i in 1..17){
                                n4testDayList.add(ntestDay.child("4st").child("ste").child("day$i").value.toString().toInt())
                            }

                            for(i in 1..4){
                                n1periodDayList.add(nperiodDay.child("1st").child("feb").child("day$i").value.toString().toInt())
                            }
                                nperiodDayList.add(nperiodDay.child("2st").child("jun").child("day1").value.toString().toInt())
                            for(i in 1..3){
                                n2periodDayList.add(nperiodDay.child("2st").child("may").child("day$i").value.toString().toInt())
                            }
                            for(i in 1..4){
                                n3periodDayList.add(nperiodDay.child("4st").child("ste").child("day$i").value.toString().toInt())
                            }
                            if(ppassDay.exists()){
                                ppassDayList.add(ppassDay.child("1st").child("may").child("day1").value.toString().toInt())
                                ppassDayList.add(ppassDay.child("2st").child("aug").child("day1").value.toString().toInt())
                                ppassDayList.add(ppassDay.child("4st").child("dec").child("day1").value.toString().toInt())

                                for(i in 1..9){
                                    p1testDayList.add(ptestDay.child("1st").child("apr").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..7){
                                    p2testDayList.add(ptestDay.child("1st").child("may").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..13){
                                    p3testDayList.add(ptestDay.child("2st").child("aug").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..3){
                                    p4testDayList.add(ptestDay.child("2st").child("jul").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..10){
                                    p5testDayList.add(ptestDay.child("4st").child("dec").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..6 ){
                                    p6testDayList.add(ptestDay.child("4st").child("nov").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..4){
                                    p1periodDayList.add(pperiodDay.child("1st").child("mar").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..4){
                                    p2periodDayList.add(pperiodDay.child("2st").child("jul").child("day$i").value.toString().toInt())
                                }
                                for(i in 1..4){
                                    p3periodDayList.add(pperiodDay.child("4st").child("oct").child("day$i").value.toString().toInt())
                                }
                                break
                            }
//                            Log.d("testday","ntestDayList"+npassDayList)
//                            Log.d("testday","ntestDayList"+ntestDayList)
//                            Log.d("testday","ntestDayList"+nperiodDayList)
//                            Log.d("testday","ntestDayList"+ppassDayList)
//                            Log.d("testday","ntestDayList"+ptestDayList)
//                            Log.d("testday","ntestDayList"+pperiodDayList)
//
                            break
                        }
                    }
                }
                    if((tempMonth==certMonth[0]) && (dayList[position].date.toString()==npassDayList[0].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==npassDayList[0].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(npassDayList[0]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                        }else if(npassDayList[0]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }


                    if((tempMonth==certMonth[1]) && (dayList[position].date.toString()==npassDayList[1].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==npassDayList[1].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(npassDayList[1]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                        }else if(npassDayList[1]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                if((tempMonth==certMonth[2]) && (dayList[position].date.toString()==npassDayList[2].toString())){
                    //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                    //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==npassDayList[2].toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                    // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                    Log.d("kim", positionList[cnt-1].toString())
                    if(npassDayList[2]<=10){
                        if(position==positionList[0])
                            binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }else if(npassDayList[2]>23){
                        if(position==positionList[1])
                            binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }else{
                        binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                    }
                    //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                    //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                }
                ////testDayList비교 3번쨰인덱스(3월과비교)
                for(k in 0..9){
                    if((tempMonth==certMonth[3]) && (dayList[position].date.toString()==n1testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n1testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(n1testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }else if(n1testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                //testDayList의 4번쨰인덱스(6월)과비교
                for(k in 0..8){
                    if((tempMonth==certMonth[4]) && (dayList[position].date.toString()==n2testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n2testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && n2testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }else if(cnt>=2&&  n2testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }//certMonth의 5번쨰인덱스(6월)과비교
                for(k in 0..17){
                    if((tempMonth==certMonth[5]) && (dayList[position].date.toString()==n3testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n3testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if (n3testDayList[k] <= 10) {
                            if (positionList.size >= 1 && position == positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        } else if (n3testDayList[k] > 21) {
                            if (positionList.size >= 2 && position == positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        } else {
                            if (positionList.size >= 1 && position == positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }//certMonth의 6번쨰인덱스와 비교
                for(k in 0..16){
                    if((tempMonth==certMonth[6]) && (dayList[position].date.toString()==n4testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n4testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && n4testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }else if(cnt>=2 && n4testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }//certMonth의 7번쨰인덱스와 비교
                for(k in 0..3){
                    if((tempMonth==certMonth[7]) && (dayList[position].date.toString()==n1periodDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n1periodDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(n1periodDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }else if(n1periodDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                //certMonth의 8번쨰인덱스와 비교
                if((tempMonth==certMonth[8]) && (dayList[position].date.toString()==nperiodDayList[0].toString())){
                    //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                    //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==nperiodDayList[0].toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                    // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                    Log.d("kim", positionList[cnt-1].toString())
                    if(nperiodDayList[0]<=10){
                        if(position==positionList[0])
                            binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                    }else if(nperiodDayList[0]>23){
                        if(position==positionList[1])
                            binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                    }else{
                        binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                    }
                    //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                    //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                }
                Log.d("minwoo","n2periodDayList : "+ n2periodDayList)
                //certMonth의 9번쨰인덱스와 비교
                for(k in 0..2){
                    if((tempMonth==certMonth[9]) && (dayList[position].date.toString()==n2periodDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n2periodDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && n2periodDayList[0]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }else if(cnt>=2 && n2periodDayList[0]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                //certMonth의 10번쨰인덱스와 비교
                for(k in 0..3){
                    if((tempMonth==certMonth[10]) && (dayList[position].date.toString()==n3periodDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==n3periodDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("min", positionList[cnt-1].toString())
                        if(n3periodDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }else if(n3periodDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }//ppassdaylist[0]
                if((tempMonth==certMonth[11]) && (dayList[position].date.toString()==ppassDayList[0].toString())){
                    //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                    //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==ppassDayList[0].toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                    // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                    Log.d("kim", positionList[cnt-1].toString())
                    if(ppassDayList[0]<=10){
                        if(position==positionList[0])
                            binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }else if(ppassDayList[0]>25){
                        if(position==positionList[1])
                            binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }else{
                        binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }
                    //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                    //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                }
                if((tempMonth==certMonth[12]) && (dayList[position].date.toString()==ppassDayList[1].toString())){
                    //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                    //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==ppassDayList[1].toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                    // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                    Log.d("kim", positionList[cnt-1].toString())
                    if(ppassDayList[1]<=10){
                        if(position==positionList[0])
                            binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }else if(ppassDayList[1]>23){
                        if(position==positionList[1])
                            binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }else{
                        binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }
                    //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                    //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                }
                if((tempMonth==certMonth[13]) && (dayList[position].date.toString()==ppassDayList[2].toString())){
                    //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                    //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                    var cnt=0
                    val positionList= mutableListOf<Int>()
                    for((i,date) in dayList.withIndex()){
                        if(date.date.toString()==ppassDayList[2].toString()){
                            ++cnt
                            positionList.add(i)
                        }
                    }
                    //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                    // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                    Log.d("kim", positionList[cnt-1].toString())
                    if(ppassDayList[2]<=10){
                        if(position==positionList[0])
                            binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }else if(ppassDayList[2]>23){
                        if(position==positionList[1])
                            binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }else{
                        binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                    }
                    //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                    //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                }
                for(k in 0..8){
                    if((tempMonth==certMonth[14]) && (dayList[position].date.toString()==p1testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p1testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && p1testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else if(cnt>=2 && p1testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..6){
                    if((tempMonth==certMonth[15]) && (dayList[position].date.toString()==p2testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p2testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(p2testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else if(p2testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..12){
                    if((tempMonth==certMonth[16]) && (dayList[position].date.toString()==p3testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p3testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(p3testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else if(p3testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..2){
                    if((tempMonth==certMonth[17]) && (dayList[position].date.toString()==p4testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p4testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && p4testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else if(cnt>=2 && p4testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..9){
                    if((tempMonth==certMonth[18]) && (dayList[position].date.toString()==p5testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p5testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(p5testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else if(p5testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..5 ){
                    if((tempMonth==certMonth[19]) && (dayList[position].date.toString()==p6testDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p6testDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && p6testDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else if(cnt>=2 && p6testDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..3){
                    if((tempMonth==certMonth[20]) && (dayList[position].date.toString()==p1periodDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p1periodDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(cnt>=2 && p1periodDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }else if(cnt>=2 && p1periodDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..3){
                    if((tempMonth==certMonth[21]) && (dayList[position].date.toString()==p2periodDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p2periodDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        Log.d("kim", positionList[cnt-1].toString())
                        if(p2periodDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }else if(p2periodDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }
                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                    }
                }
                for(k in 0..3){
                    if((tempMonth==certMonth[22]) && (dayList[position].date.toString()==p3periodDayList[k].toString())){
                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                        var cnt=0
                        val positionList= mutableListOf<Int>()
                        for((i,date) in dayList.withIndex()){
                            if(date.date.toString()==p3periodDayList[k].toString()){
                                ++cnt
                                positionList.add(i)
                            }
                        }
                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                        if(cnt>=2 && p3periodDayList[k]<=10){
                            if(position==positionList[0])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }else if(cnt>=2 && p3periodDayList[k]>23){
                            if(position==positionList[1])
                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }else{
                            binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                        }

                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
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