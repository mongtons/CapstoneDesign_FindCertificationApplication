package hallym.capstone.findcertificateapplication.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.CertificationActivity
import hallym.capstone.findcertificateapplication.MainActivity
import hallym.capstone.findcertificateapplication.QuestionActivity
import hallym.capstone.findcertificateapplication.databinding.ListItemDayBinding
import hallym.capstone.findcertificateapplication.datatype.Examdaydata
import java.util.*

val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
val database: FirebaseDatabase = FirebaseDatabase.getInstance()
val ref: DatabaseReference =database.getReference()
//var reftitle = ArrayList<String>(3)
var reffavorite = String
var cerTitle : String = ""



class ViewHolder(val binding : ListItemDayBinding):RecyclerView.ViewHolder(binding.root)

@Suppress("DEPRECATION")
class AdapterDay(
    val tempMonth: Int,
    val dayList: MutableList<Date>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ROW = 6

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(ListItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val binding = (holder as ViewHolder).binding

        // 클릭 이벤트 리스너 설정
        binding.itemDayLayout.setOnClickListener {
//            Toast.makeText(binding.itemDayLayout.context,
////                "${dayList[position]}",
//                "${holder.adapterPosition}",
//                Toast.LENGTH_SHORT
//            ).show()
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
                            cerTitle = ds.child("cerTitle").value.toString()

                        }
                    }
                }

                // 자격증 목록 가져오기
                val certification = snapshot.child("Certification")
                for (data in certification.children) {
                    val title = data.child("title")
                    if (title.value.toString() == cerTitle) {
                        val nonperiodList = mutableListOf<List<Int>>()
                        val nontestList = mutableListOf<List<Int>>()
                        val nonpassList = mutableListOf<List<Int>>()
                        val nopperiodList = mutableListOf<List<Int>>()
                        val noptestList = mutableListOf<List<Int>>()
                        val noppassList = mutableListOf<List<Int>>()

                        val nperiodList = mutableListOf<List<Int>>()
                        var ntestList = mutableListOf<List<Int>>()
                        val npassList= mutableListOf<List<Int>>()
                        val pperiodList = mutableListOf<List<Int>>()
                        val ptestList = mutableListOf<List<Int>>()
                        val ppassList= mutableListOf<List<Int>>()

                        val nonpass = data.child("testDay").child("pass")
                        val noppass = data.child("testDay").child("pass")
                        val nonperiod = data.child("testDay").child("period")
                        val nopperiod = data.child("testDay").child("period")
                        val nontest = data.child("testDay").child("test")
                        val noptest = data.child("testDay").child("test")

                        val npass = data.child("testDay").child("pass").child("note")
                        val ppass = data.child("testDay").child("pass").child("practice")
                        val nperiod = data.child("testDay").child("period").child("note")
                        val pperiod = data.child("testDay").child("period").child("practice")
                        val ntest = data.child("testDay").child("test").child("note")
                        val ptest = data.child("testDay").child("test").child("practice")

                        val q: Queue<Int> = LinkedList<Int>()
                        if (ntest.hasChildren()) {
                            ntestList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            for (i in 1..4) {
                                val child = ntest.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for (month in months) {
                                                for (date in child.child(month).children) {
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if (date.key.toString() == "month") {
                                                        val daymonthList = mutableListOf<Int>()
                                                        for (k in 0..q.size - 1) {
                                                            daymonthList.add(q.poll())
                                                        }

                                                        if (ntestList.size == 0) {
                                                            ntestList.add(daymonthList)
                                                        }

                                                        for (j in 0 until ntestList.size) {
                                                            if (ntestList[j] == daymonthList) {
                                                                break
                                                            } else if (j == ntestList.size - 1) {
                                                                ntestList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for (w in 0..ntestList.size - 1) {
                                for (n in 0..ntestList[w].size - 2) {
                                    if ((tempMonth == ntestList[w][ntestList[w].size - 1] - 1) && (dayList[position].date.toString() == ntestList[w][n].toString())) {
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt = 0
                                        val positionList = mutableListOf<Int>()
                                        for ((i, date) in dayList.withIndex()) {
                                            if (date.date.toString() == ntestList[w][n].toString()) {
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt - 1].toString())
                                        if (cnt >= 2 && ntestList[w][n] <= 12) {
                                            if (position == positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                }
                                            }
                                        } else if (cnt >= 2 && ntestList[w][n] > 23) {
                                            if (position == positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                }
                                            }
                                        } else {
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#0000CC"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                                            }

                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }//note와 practice가 있을때

                        } else {
                            nontestList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q.clear()
                            for (i in 1..4) {
                                val child = nontest.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for (month in months) {
                                                for (date in child.child(month).children) {
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if (date.key.toString() == "month") {
                                                        val daymonthList = mutableListOf<Int>()
                                                        for (k in 0..q.size - 1) {
                                                            daymonthList.add(q.poll())
                                                        }

                                                        if (nontestList.size == 0) {
                                                            nontestList.add(daymonthList)
                                                        }

                                                        for (j in 0 until nontestList.size) {
                                                            if (nontestList[j] == daymonthList) {
                                                                break
                                                            } else if (j == nontestList.size - 1) {
                                                                nontestList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for (w in 0..nontestList.size - 1) {
                                for (n in 0..nontestList[w].size - 2) {
                                    if ((tempMonth == nontestList[w][nontestList[w].size - 1] - 1) && (dayList[position].date.toString() == nontestList[w][n].toString())) {
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt = 0
                                        val positionList = mutableListOf<Int>()
                                        for ((i, date) in dayList.withIndex()) {
                                            if (date.date.toString() == nontestList[w][n].toString()) {
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt - 1].toString())
                                        if (cnt >= 2 && nontestList[w][n] <= 12) {
                                            if (position == positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                }
                                            }
                                        } else if (cnt >= 2 && nontestList[w][n] > 23) {
                                            if (position == positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                                                }
                                            }
                                        } else {
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#0000CC"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#0000CC"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        }
                        val q1: Queue<Int> = LinkedList<Int>()
                        if (nperiod.hasChildren()) {
                            nperiodList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q1.clear()
                            for (i in 1..4) {
                                val child = nperiod.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for (month in months) {
                                                for (date in child.child(month).children) {
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q1.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if (date.key.toString() == "month") {
                                                        val daymonthList = mutableListOf<Int>()
                                                        for (k in 0..q1.size - 1) {
                                                            daymonthList.add(q1.poll())
                                                        }

                                                        if (nperiodList.size == 0) {
                                                            nperiodList.add(daymonthList)
                                                        }

                                                        for (j in 0 until nperiodList.size) {
                                                            if (nperiodList[j] == daymonthList) {
                                                                break
                                                            } else if (j == nperiodList.size - 1) {
                                                                nperiodList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$nperiodList")

                            for (w in 0..nperiodList.size - 1) {
                                for (n in 0..nperiodList[w].size - 2) {
                                    if ((tempMonth == nperiodList[w][nperiodList[w].size - 1] - 1) && (dayList[position].date.toString() == nperiodList[w][n].toString())) {
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt = 0
                                        val positionList = mutableListOf<Int>()
                                        for ((i, date) in dayList.withIndex()) {
                                            if (date.date.toString() == nperiodList[w][n].toString()) {
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt - 1].toString())
                                        if (cnt >= 2 && nperiodList[w][n] <= 12) {
                                            if (position == positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#000000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                                                }

                                            }
                                        } else if (cnt >= 2 && nperiodList[w][n] > 23) {
                                            if (position == positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#000000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                                                }

                                            }
                                        } else {
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#000000"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        } else {
                            nonperiodList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q1.clear()
                            for (i in 1..4) {
                                val child = nonperiod.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for (month in months) {
                                                for (date in child.child(month).children) {
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q1.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if (date.key.toString() == "month") {
                                                        val daymonthList = mutableListOf<Int>()
                                                        for (k in 0..q1.size - 1) {
                                                            daymonthList.add(q1.poll())
                                                        }

                                                        if (nonperiodList.size == 0) {
                                                            nonperiodList.add(daymonthList)
                                                        }

                                                        for (j in 0 until nonperiodList.size) {
                                                            if (nonperiodList[j] == daymonthList) {
                                                                break
                                                            } else if (j == nonperiodList.size - 1) {
                                                                nonperiodList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$nonperiodList")

                            for (w in 0..nonperiodList.size - 1) {
                                for (n in 0..nonperiodList[w].size - 2) {
                                    if ((tempMonth == nonperiodList[w][nonperiodList[w].size - 1] - 1) && (dayList[position].date.toString() == nonperiodList[w][n].toString())) {
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt = 0
                                        val positionList = mutableListOf<Int>()
                                        for ((i, date) in dayList.withIndex()) {
                                            if (date.date.toString() == nonperiodList[w][n].toString()) {
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt - 1].toString())
                                        if (cnt >= 2 && nonperiodList[w][n] <= 12) {
                                            if (position == positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#000000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                                                }

                                            }
                                        } else if (cnt >= 2 && nonperiodList[w][n] > 23) {
                                            if (position == positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#000000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                                                }

                                            }
                                        } else {
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#000000"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#000000"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        }
                        val q2:Queue<Int> = LinkedList<Int>()
                        if (npass.hasChildren()) {
                            npassList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q2.clear()
                            for (i in 1..4) {
                                val child = npass.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q2.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q2.size - 1){
                                                            daymonthList.add(q2.poll())
                                                        }

                                                        if(npassList.size == 0){
                                                            npassList.add(daymonthList)
                                                        }

                                                        for (j in 0 until npassList.size) {
                                                            if (npassList[j] == daymonthList) {
                                                                break
                                                            } else if (j == npassList.size - 1) {
                                                                npassList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$npassList")

                            for(w in 0..npassList.size-1){
                                for(n in 0..npassList[w].size-2){
                                    if((tempMonth==npassList[w][npassList[w].size- 1]-1) && (dayList[position].date.toString()==npassList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==npassList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())
                                        if(cnt>=2 && npassList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                }

                                            }
                                        }else if(cnt>=2 && npassList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        }else{
                            nonpassList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q2.clear()
                            for (i in 1..4) {
                                val child = nonpass.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q2.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q2.size - 1){
                                                            daymonthList.add(q2.poll())
                                                        }

                                                        if(nonpassList.size == 0){
                                                            nonpassList.add(daymonthList)
                                                        }

                                                        for (j in 0 until nonpassList.size) {
                                                            if (nonpassList[j] == daymonthList) {
                                                                break
                                                            } else if (j == nonpassList.size - 1) {
                                                                nonpassList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$nonpassList")

                            for(w in 0..nonpassList.size-1){
                                for(n in 0..nonpassList[w].size-2){
                                    if((tempMonth==nonpassList[w][nonpassList[w].size- 1]-1) && (dayList[position].date.toString()==nonpassList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==nonpassList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())
                                        if(cnt>=2 && nonpassList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                }

                                            }
                                        }else if(cnt>=2 && nonpassList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#FF00FF"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }

                        }
                        val q3:Queue<Int> = LinkedList<Int>()
                        if (ptest.hasChildren()) {
                            ptestList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q3.clear()
                            for (i in 1..4) {
                                val nochild = noptest.child("i"+"st")
                                val child = ptest.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q3.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q3.size - 1){
                                                            daymonthList.add(q3.poll())
                                                        }

                                                        if(ptestList.size == 0){
                                                            ptestList.add(daymonthList)
                                                        }

                                                        for (j in 0 until ptestList.size) {
                                                            if (ptestList[j] == daymonthList) {
                                                                break
                                                            } else if (j == ptestList.size - 1) {
                                                                ptestList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for(w in 0..ptestList.size-1){
                                for(n in 0..ptestList[w].size-2){
                                    if((tempMonth==ptestList[w][ptestList[w].size- 1]-1) && (dayList[position].date.toString()==ptestList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==ptestList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())
                                        if(cnt>=2 && ptestList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                }
                                            }
                                        }else if(cnt>=2 && ptestList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#00FF00"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        }else{
                            noptestList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q3.clear()
                            for (i in 1..4) {
                                val child = noptest.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q3.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q3.size - 1){
                                                            daymonthList.add(q3.poll())
                                                        }

                                                        if(noptestList.size == 0){
                                                            noptestList.add(daymonthList)
                                                        }

                                                        for (j in 0 until noptestList.size) {
                                                            if (noptestList[j] == daymonthList) {
                                                                break
                                                            } else if (j == noptestList.size - 1) {
                                                                noptestList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for(w in 0..noptestList.size-1){
                                for(n in 0..noptestList[w].size-2){
                                    if((tempMonth==noptestList[w][noptestList[w].size- 1]-1) && (dayList[position].date.toString()==noptestList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==noptestList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())
                                        if(cnt>=2 && noptestList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                }
                                            }
                                        }else if(cnt>=2 && noptestList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#00FF00"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#00FF00"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }

                        }

                        val q4:Queue<Int> = LinkedList<Int>()
                        if (pperiod.hasChildren()) {
                            pperiodList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q4.clear()
                            for (i in 1..4) {
                                val child = pperiod.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q4.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q4.size - 1){
                                                            daymonthList.add(q4.poll())
                                                        }

                                                        if(pperiodList.size == 0){
                                                            pperiodList.add(daymonthList)
                                                        }

                                                        for (j in 0 until pperiodList.size) {
                                                            if (pperiodList[j] == daymonthList) {
                                                                break
                                                            } else if (j == pperiodList.size - 1) {
                                                                pperiodList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for(w in 0..pperiodList.size-1){
                                for(n in 0..pperiodList[w].size-2){
                                    if((tempMonth==pperiodList[w][pperiodList[w].size- 1]-1) && (dayList[position].date.toString()==pperiodList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==pperiodList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())

                                        if(cnt>=2 && pperiodList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                }
                                            }

                                        }else if(cnt>=2 && pperiodList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#FF0000"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        }else{
                            nopperiodList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q4.clear()
                            for (i in 1..4) {
                                val child = nopperiod.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q4.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q4.size - 1){
                                                            daymonthList.add(q4.poll())
                                                        }

                                                        if(nopperiodList.size == 0){
                                                            nopperiodList.add(daymonthList)
                                                        }

                                                        for (j in 0 until nopperiodList.size) {
                                                            if (nopperiodList[j] == daymonthList) {
                                                                break
                                                            } else if (j == nopperiodList.size - 1) {
                                                                nopperiodList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            for(w in 0..nopperiodList.size-1){
                                for(n in 0..nopperiodList[w].size-2){
                                    if((tempMonth==nopperiodList[w][nopperiodList[w].size- 1]-1) && (dayList[position].date.toString()==nopperiodList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==nopperiodList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())

                                        if(cnt>=2 && nopperiodList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                }
                                            }

                                        }else if(cnt>=2 && nopperiodList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#FF0000"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#FF0000"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }

                        }

                        val q5:Queue<Int> = LinkedList<Int>()
                        if (ppass.hasChildren()) {
                            ppassList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q5.clear()
                            for (i in 1..4) {
                                val child = ppass.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q5.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q5.size - 1){
                                                            daymonthList.add(q5.poll())
                                                        }

                                                        if(ppassList.size == 0){
                                                            ppassList.add(daymonthList)
                                                        }

                                                        for (j in 0 until ppassList.size) {
                                                            if (ppassList[j] == daymonthList) {
                                                                break
                                                            } else if (j == ppassList.size - 1) {
                                                                ppassList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for(w in 0..ppassList.size-1){
                                for(n in 0..ppassList[w].size-2){
                                    if((tempMonth==ppassList[w][ppassList[w].size- 1]-1) && (dayList[position].date.toString()==ppassList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==ppassList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())
                                        if(cnt>=2 && ppassList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                }
                                            }
                                        }else if(cnt>=2 && ppassList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }
                        }else{
                            noppassList.clear()
                            val months = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
                            q5.clear()
                            for (i in 1..4) {
                                val child = noppass.child("$i" + "st")
                                if (child.hasChildren()) {
                                    child.children.forEach { monthChild ->
                                        val month = months.indexOf(monthChild.key)
                                        if (month >= 0) {
                                            for(month in months) {
                                                for(date in child.child(month).children){
                                                    //Log.d("cclo", "$i 번째 : " + date.value)
                                                    q5.add(date.value.toString().toInt())
                                                    //Log.d("cclo", "$q")
                                                    if(date.key.toString() == "month"){
                                                        val daymonthList =  mutableListOf<Int>()
                                                        for( k in 0..q5.size - 1){
                                                            daymonthList.add(q5.poll())
                                                        }

                                                        if(noppassList.size == 0){
                                                            noppassList.add(daymonthList)
                                                        }

                                                        for (j in 0 until noppassList.size) {
                                                            if (noppassList[j] == daymonthList) {
                                                                break
                                                            } else if (j ==noppassList.size - 1) {
                                                                noppassList.add(daymonthList)
                                                            }
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            Log.d("cclo", "$ntestList")

                            for(w in 0..noppassList.size-1){
                                for(n in 0..noppassList[w].size-2){
                                    if((tempMonth==noppassList[w][noppassList[w].size- 1]-1) && (dayList[position].date.toString()==noppassList[w][n].toString())){
                                        //AdapterMonth에서 받아온 certMonth(해당 자격증 일정의 월)과 현 달력의 월이 같은 지 확인
                                        //dayList의 date를 이용하고 데이터베이스에서 받아온 일(testDay1)을 둘이 비교하여 같은 날이 있는 지 확인
                                        var cnt=0
                                        val positionList= mutableListOf<Int>()
                                        for((i,date) in dayList.withIndex()){
                                            if(date.date.toString()==noppassList[w][n].toString()){
                                                ++cnt
                                                positionList.add(i)
                                            }
                                        }
                                        //달력에는 앞전 달의 일과 뒷 달의 일이 같이 출력되므로 해당 일이 2번 이상 존재하는 지 체크하고
                                        // 알맞은 일인 dayList의 position을 positionList에 넣어 어디에 색칠할 지 구분할 때 사용

                                        Log.d("kim", positionList[cnt-1].toString())
                                        if(cnt>=2 && noppassList[w][n]<=12){
                                            if(position==positionList[0]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                }
                                            }
                                        }else if(cnt>=2 && noppassList[w][n]>23){
                                            if(position==positionList[1]) {
                                                if (binding.oneday.background != null) {
                                                    binding.twoday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                } else {
                                                    binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                                }
                                            }
                                        }else{
                                            if (binding.oneday.background != null) {
                                                binding.twoday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                            } else {
                                                binding.oneday.setBackgroundColor(Color.parseColor("#FFFF00"))
                                            }
                                        }
                                        //testDay1이 10일 이하 일 때 먼저 저장했던 position을 사용
                                        //testDay1이 20일 이상 일 때 나중에 저장했던 position을 사용
                                    }
                                }
                            }

                        }

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
