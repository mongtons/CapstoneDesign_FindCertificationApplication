package hallym.capstone.findcertificateapplication.examdayfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentTestBinding
import hallym.capstone.findcertificateapplication.datatype.ExamdayResult
import hallym.capstone.findcertificateapplication.datatype.Examdaydata
import java.util.*

class TestFragment(title:String) : Fragment() {
    lateinit var binding:FragmentTestBinding
    val database= FirebaseDatabase.getInstance()
    val ref=database.getReference("Certification")
    val certTitle=title
    val find=PeriodFragment(title)
    val examDayList= mutableListOf<Examdaydata>()
    val dateList= mutableListOf<Examdaydata>()
    val resultList= mutableListOf<ExamdayResult>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTestBinding.inflate(layoutInflater, container, false)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(cert in snapshot.children){
                    if(cert.child("title").value.toString()==certTitle){
                        val period=cert.child("testDay").child("test")
                        if(period.hasChild("note") || period.hasChild("practice")) {
                            for (date in period.children) {
                                for (i in 1..4) {
                                    find.findMonth(date.child("${i}st"), date.key.toString(), examDayList)
                                }
                            }
                        }else{
                            for (date in period.children) {
                                find.findMonth(date, "", examDayList)
                            }
                        }
                        var flag=false
                        dateList.add(examDayList[0])
                        for(i in 1 until examDayList.size){
                            if(flag) {
                                dateList.add(examDayList[i])
                                flag = false
                            }
                            if(examDayList[i].month){
                                if(dateList[dateList.lastIndex].date!=examDayList[i-1].date)
                                    dateList.add(examDayList[i-1])
                                dateList.add(examDayList[i])
                                flag=true
                            }
                        }
                        dateList.add(examDayList[examDayList.size-1])

                        val q: Queue<Examdaydata> = LinkedList<Examdaydata>()
                        for(i in 0 until dateList.size){
                            if(dateList[i].month){
                                if(q.size>=2){
                                    resultList.add(
                                        ExamdayResult(
                                            dateList[i].no,
                                            dateList[i].type,
                                            "${dateList[i].date}월 ${q.poll()?.date}일~${dateList[i].date}월 ${q.poll()?.date}일"
                                        )
                                    )
                                }else{
                                    resultList.add(
                                        ExamdayResult(
                                            dateList[i].no,
                                            dateList[i].type,
                                            "${dateList[i].date}월 ${q.poll()?.date}일"
                                        )
                                    )
                                }
                            }else {
                                q.offer(dateList[i])
                                Log.d("kim", q.toString()+" size: ${q.size}")
                            }
                        }
                        resultList.removeAt(resultList.lastIndex)
                    }
                    val layoutmanager= LinearLayoutManager(context)
                    layoutmanager.orientation= LinearLayoutManager.VERTICAL
                    binding.testRecycler.layoutManager=layoutmanager
                    binding.testRecycler.adapter=ExamDayAdapter(resultList)
                }
//                Log.d("kim", examDayList.toString())
                Log.d("kim", resultList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_:java.lang.Exception){ }
            }
        })

        return binding.root
    }
}