package hallym.capstone.findcertificateapplication.examdayfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hallym.capstone.findcertificateapplication.databinding.ExamdayItemBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentPeriodBinding
import hallym.capstone.findcertificateapplication.datatype.ExamdayResult
import hallym.capstone.findcertificateapplication.datatype.Examdaydata
import java.util.LinkedList
import java.util.Queue

class PeriodFragment(title:String) : Fragment() {
    lateinit var binding:FragmentPeriodBinding
    val database=FirebaseDatabase.getInstance()
    val ref=database.getReference("Certification")
    val certTitle=title
    val examDayList= mutableListOf<Examdaydata>()
    val dateList= mutableListOf<Examdaydata>()
    val resultList= mutableListOf<ExamdayResult>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPeriodBinding.inflate(layoutInflater, container, false)

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(cert in snapshot.children){
                    if(cert.child("title").value.toString()==certTitle){
                        val period=cert.child("testDay").child("period")
                        if(period.hasChild("note") || period.hasChild("practice")) {
                            for (date in period.children) {
                                for (i in 1..4) {
                                    findMonth(date.child("${i}st"), date.key.toString(), examDayList)
                                }
                            }
                        }else{
                            for (date in period.children) {
                                findMonth(date, "", examDayList)
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

                        val q:Queue<Examdaydata> = LinkedList<Examdaydata>()
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
                    val layoutmanager=LinearLayoutManager(context)
                    layoutmanager.orientation=LinearLayoutManager.VERTICAL
                    binding.periodRecycler.layoutManager=layoutmanager
                    binding.periodRecycler.adapter=ExamDayAdapter(resultList)
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
    fun findMonth(snapshot: DataSnapshot, type:String, examDayList:MutableList<Examdaydata>){
        val monthList= listOf<String>("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "ste", "oct", "nov", "dec")
        val no=when(snapshot.key.toString()){
            "1st"->"1회"
            "2st"->"2회"
            "3st"->"3회"
            "4st"->"4회"
            else ->""
        }
        for(month in monthList) {
            for(date in snapshot.child(month).children){
                examDayList.add(
                    if(date.key=="month"){
                        when(type) {
                            "note" -> Examdaydata(no, "필기", date.value.toString(), true)
                            "practice" -> Examdaydata(no, "실기", date.value.toString(), true)
                            else -> Examdaydata(no, "", date.value.toString(), true)
                        }
                    }else {
                        when(type) {
                            "note" -> Examdaydata(no, "필기", date.value.toString(), false)
                            "practice" -> Examdaydata(no, "실기", date.value.toString(), false)
                            else -> Examdaydata(no, "", date.value.toString(), false)
                        }
                    }
                )
            }
        }
    }
}
class ExamDayViewHolder(val binding:ExamdayItemBinding):RecyclerView.ViewHolder(binding.root)
class ExamDayAdapter(val contents: MutableList<ExamdayResult>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ExamDayViewHolder(ExamdayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding=(holder as ExamDayViewHolder).binding
        binding.examNo.text=contents[position].no
        binding.examType.text=contents[position].type
        binding.examdayText.text=contents[position].date

    }

    override fun getItemCount(): Int {
        return contents.size
    }
}
